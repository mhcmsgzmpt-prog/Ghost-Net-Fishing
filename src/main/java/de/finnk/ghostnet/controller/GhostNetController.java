package de.finnk.ghostnet.controller;

import de.finnk.ghostnet.model.GhostNet;
import de.finnk.ghostnet.model.GhostNetStatus;
import de.finnk.ghostnet.service.GhostNetService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import de.finnk.ghostnet.model.ReportingPerson;
import de.finnk.ghostnet.model.RecoveringPerson;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;


@Controller
public class GhostNetController {
    private final GhostNetService ghostNetService;

    private static final String ADMIN_USERNAME = "ADMIN";
    private static final String ADMIN_PASSWORD = "12345";

    private static final String ADMIN_SESSION_ATTRIBUTE = "isAdmin";

    public GhostNetController(GhostNetService ghostNetService) {
        this.ghostNetService = ghostNetService;
    }

    @GetMapping("/")
    public String redirectToGhostNets() {
        return "redirect:/ghostnets";
    }


    @GetMapping("/ghostnets")
    public String getAllGhostNets(Model model, HttpSession session) {
        List<GhostNet> ghostNets = ghostNetService.getAllGhostNets();

        List<GhostNet> mapGhostNets = ghostNets.stream()
            .filter(ghostNet -> ghostNet.getStatus() != GhostNetStatus.RECOVERED 
                            && ghostNet.getStatus() != GhostNetStatus.MISSING).toList();

        model.addAttribute("ghostNets", ghostNets);
        model.addAttribute("mapGhostNets", mapGhostNets);
        model.addAttribute("isAdmin", isAdmin(session));

        return "ghostnets";
    }

    @GetMapping("/ghostnets/new")
    public String showCreateGhostNetForm(Model model) {
        GhostNet ghostNet = new GhostNet();
        ghostNet.setReportingPerson(new ReportingPerson());
        model.addAttribute("ghostNet", ghostNet);
        return "ghostnet-form";
    }
    

    @GetMapping("/ghostnets/{id}")
    public String getGhostNetById(@PathVariable Long id, Model model) {
        GhostNet ghostNet = ghostNetService.getGhostNetById(id);

        boolean canChangeStatus = ghostNet.getRecoveringPerson() != null 
        && ghostNet.getStatus() != GhostNetStatus.RECOVERED;

        List<GhostNetStatus> selectableStatuses =
            Arrays.stream(GhostNetStatus.values())
            .filter(status -> status != GhostNetStatus.MISSING)
            .toList();
        model.addAttribute("ghostNet", ghostNet);
        model.addAttribute("canChangeStatus", canChangeStatus);

        model.addAttribute("statuses", selectableStatuses);
        return "ghostnet-details";
    }

    @GetMapping("/ghostnets/{id}/missing")
    public String showMissingGhostNetForm(@PathVariable Long id, Model model) {
        GhostNet ghostNet = ghostNetService.getGhostNetById(id);
        model.addAttribute("ghostNet", ghostNet);
        model.addAttribute("reportingPerson", new ReportingPerson());
        return "ghostnet_missing_form";
    }

    @GetMapping("/ghostnets/{id}/recover")
    public String showRecoverGhostNetForm(@PathVariable Long id, Model model) {
        GhostNet ghostNet = ghostNetService.getGhostNetById(id);
        model.addAttribute("ghostNet", ghostNet);
        model.addAttribute("recoveringPerson", new RecoveringPerson());
        return "recovering-person-form";
    }


    @PostMapping("/ghostnets")
    public String createGhostNet(@Valid @ModelAttribute("ghostNet") GhostNet ghostNet,
     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "ghostnet-form";
        }
        ghostNetService.createGhostNet(ghostNet);
        return "redirect:/ghostnets";
    }

    @PostMapping("/ghostnets/{id}/status")
    public String updateGhostNetStatus(@PathVariable Long id, @RequestParam GhostNetStatus status, RedirectAttributes redirectAttributes) {
        
        GhostNet ghostNet = ghostNetService.getGhostNetById(id);

        if (ghostNet.getRecoveringPerson()== null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Der Status kann erst gerändert werden, "
                                                                + "wenn eine bergungsperson eingetragen wurde");
            return "redirect:/ghostnets/" + id;
        }
        try {
            ghostNetService.updateGhostNetStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Der Status wurde erfolgreich geändert.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/ghostnets/" + id;
    }

    @PostMapping("/ghostnets/{id}/missing")
    public String reportMissingGhostNet(@PathVariable Long id, @ModelAttribute("reportingPerson") ReportingPerson reportingPerson,
     RedirectAttributes redirectAttributes) {

        try{
            ghostNetService.reportMissingGhostNet(id, reportingPerson);
            redirectAttributes.addFlashAttribute("successMessage", "Das Geisternetz wurde erfolgreich als vermisst gemeldet.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/ghostnets/" + id;
    }

    @PostMapping("/ghostnets/{id}/recover")
    public String assignRecoveringPerson(@PathVariable Long id, @ModelAttribute("recoveringPerson")
    RecoveringPerson recoveringPerson, RedirectAttributes redirectAttributes) {
        try {
            GhostNet ghostNet = ghostNetService.assignRecoveringPerson(id, recoveringPerson);
           
            redirectAttributes.addFlashAttribute("successMessage", "Sie wurden erfolgreich als Bergungsperson eingetragen.");
            return "redirect:/ghostnets/" + id;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/ghostnets/" + id + "/recover";
        }
        
    }
    //Anmeldung zum Admin für Sonderrechte

    @GetMapping("/admin/login")
    public String showAdminLoginForm(HttpSession session) {

        //Bereits angemeldete Admins müssen sich nicht erneut anmelden.
        if (isAdmin(session)) {
            return "redirect:/ghostnets";
        }
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String loginAdmin(@RequestParam String username, @RequestParam String password,
    HttpSession session, RedirectAttributes redirectAttributes) {
        boolean validCredentials = ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
        if (!validCredentials) {
            redirectAttributes.addFlashAttribute(
                "errorMessage", "Benutzername oder Passwort ist falsch.");
        return "redirect:/admin/login";
        }

        session.setAttribute(ADMIN_SESSION_ATTRIBUTE, true);
        redirectAttributes.addFlashAttribute(
            "successMessage", "Die Anmeldung war erfolgreich");
        return "redirect:/ghostnets";
    }

    @PostMapping("/admin/logout")
    public String logoutAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(ADMIN_SESSION_ATTRIBUTE);
        redirectAttributes.addFlashAttribute(
            "successMessage", "Sie wurden abgemeldet");
        return "redirect:/ghostnets";
    }

    @PostMapping("/admin/ghostnets/{id}/delete")
    public String deleteGhostNet(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute(
                "errorMessage", "Nur Administratoren können Geisternetze löschen.");
        return "redirect:/admin/login";
        }
        try {ghostNetService.deleteGhostNet(id);
        redirectAttributes.addFlashAttribute(
            "successMessage", "Das Gesiternetz wurde gelöscht.");
    } catch (IllegalArgumentException | IllegalStateException exception){
        redirectAttributes.addFlashAttribute(
            "errorMessage", "exception.getMessage()"
        );
    }
    return "redirect:/ghostnets";
    }

    private boolean isAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_ATTRIBUTE));
    }

}