package com.institute.service;

import com.institute.model.Notice;
import com.institute.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository repo;

    public Notice addNotice(Notice n) {

        if (n.getDate() == null) {
            n.setDate(LocalDate.now());
        }
        return repo.save(n);
    }

    public List<Notice> getAll() {
        return repo.findAllByOrderByDateDescIdDesc();
    }

    public List<Notice> getLatest() {
        return repo.findTop5ByOrderByDateDescIdDesc();
    }

    public boolean deleteNotice(Long id) {
        if (!repo.existsById(id)) return false;

        repo.deleteById(id);
        return true;
    }

    public Notice updateNotice(Long id, Notice updated) {
        Notice n = repo.findById(id).orElse(null);

        if (n == null) throw new RuntimeException("Notice not found");

        n.setTitle(updated.getTitle());
        n.setMessage(updated.getMessage());
        n.setDate(updated.getDate());
        n.setDayOfWeek(updated.getDayOfWeek());

        return repo.save(n);
    }

}
