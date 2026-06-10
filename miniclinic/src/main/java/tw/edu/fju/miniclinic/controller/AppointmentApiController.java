package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class AppointmentApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @PostMapping("/api/appointments")
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Map<String, String> request) {

        String chartNo = request.get("chartNo");
        String doctorId = request.get("doctorId");
        LocalDate apptDate = LocalDate.parse(request.get("apptDate"));
        String timeSlot = request.get("timeSlot");

        Patient patient = patientRepo.findById(chartNo).orElse(null);
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (patient == null || doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(apptDate);
        appt.setTimeSlot(timeSlot);
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/api/appointments/count")
    public ResponseEntity<Map<String, Long>> getAppointmentsCount() {
        return ResponseEntity.ok(Map.of("count", appointmentRepo.count()));
    }

    @GetMapping("/api/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {
        
        if (date != null && !date.isEmpty()) {
            return ResponseEntity.ok(appointmentRepo.findByApptDate(LocalDate.parse(date)));
        } else if (doctorId != null && !doctorId.isEmpty()) {
            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            if (doctor != null) {
                return ResponseEntity.ok(appointmentRepo.findByDoctor(doctor));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.ok(appointmentRepo.findAll());
        }
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");

        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }

        // 只能修改自己的掛號
        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build();
        }

        String newStatus = payload.get("status");
        if (!List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().build();
        }

        appt.setStatus(newStatus);
        return ResponseEntity.ok(appointmentRepo.save(appt));
    }
}