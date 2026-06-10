package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StatsController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/stats")
    public String showStats(Model model) {
        // 基本總數統計
        model.addAttribute("doctorCount", doctorRepo.count());
        model.addAttribute("patientCount", patientRepo.count());
        model.addAttribute("appointmentCount", appointmentRepo.count());

        // 在 Controller 中程式化計算依科別分組的掛號數
        Map<String, Long> deptStats = appointmentRepo.findAll().stream()
                .filter(appt -> appt.getDoctor() != null && appt.getDoctor().getDepartment() != null)
                .collect(Collectors.groupingBy(
                        appt -> appt.getDoctor().getDepartment(),
                        Collectors.counting()
                ));
        model.addAttribute("deptStats", deptStats);

        return "stats";
    }
}