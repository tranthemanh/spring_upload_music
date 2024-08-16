package com.manhcode.controller;

import com.manhcode.model.Song;
import com.manhcode.model.SongForm;
import com.manhcode.service.HibernateSongService;
import com.manhcode.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/songs")
@PropertySource("classpath:upload_file.properties")
public class SongController {
    @Autowired
    private ISongService songService;

    @GetMapping("")
    public String index(Model model) {
        List<Song> songs = songService.findAll();
        model.addAttribute("songs", songs);
        return "/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("song", new Song());
        return "/create";
    }

    @Value("${file-upload}")
    private String upload;

    @PostMapping("/save")
    public String save(SongForm songForm) {
        MultipartFile file = songForm.getFilePath();
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(songForm.getFilePath().getBytes(), new File(upload + fileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Song song = new Song();
        song.setName(songForm.getName());
        song.setArtist(songForm.getArtist());
        song.setGenres(songForm.getGenres());
        song.setFilePath(fileName);
        songService.save(song);
        return "redirect:/songs";
    }

    @GetMapping("/{id}/edit")
    public String update(@PathVariable int id, Model model) {
        model.addAttribute("song", songService.findById(id));
        return "/update";
    }

    @PostMapping("/update")
    public String update(SongForm songForm) {
        MultipartFile file = songForm.getFilePath();
        String fileName = file.getOriginalFilename();
        try {
            FileCopyUtils.copy(file.getBytes(), new File(upload+fileName));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Song song = new Song();
        song.setName(songForm.getName());
        song.setArtist(songForm.getArtist());
        song.setGenres(songForm.getGenres());
        song.setFilePath(fileName);
        songService.update(song.getId(), song);
        return "redirect:/songs";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable int id, Model model) {
        model.addAttribute("song", songService.findById(id));
        return "/delete";
    }

    @PostMapping("/delete")
    public String delete(Song song, RedirectAttributes redirect) {
        songService.remove(song.getId());
        redirect.addFlashAttribute("success", "Removed customer successfully!");
        return "redirect:/songs";
    }

    @GetMapping("/{id}/view")
    public String view(@PathVariable int id, Model model) {
        model.addAttribute("song", songService.findById(id));
        return "/view";
    }
}
