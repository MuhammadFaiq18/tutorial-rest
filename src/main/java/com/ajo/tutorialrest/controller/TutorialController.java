package com.ajo.tutorialrest.controller;

import com.ajo.tutorialrest.model.Tutorial;
import com.ajo.tutorialrest.repository.TutorialRepository;
import com.ajo.tutorialrest.response.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping
public class TutorialController {

    @Autowired
    TutorialRepository tutorialRepository;

    @GetMapping("/test")
    public String test() {
        return "masuk gan!";
    }

    @GetMapping("/tutorials")
    public ResponseEntity<Object> getAllTutorials(@RequestParam(required = false) String title) {
        try {
            List<Tutorial> tutorials = new ArrayList<Tutorial>();

            if (title == null) {
                tutorialRepository.findAll().forEach(tutorials::add);
            } else {
                tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);
            }

            if (tutorials.isEmpty()) {
                String response = "NO_CONTENT";
                return ResponseHandler.generateResponse(response, HttpStatus.OK, null);
            }

            return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, tutorials);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Object> getTutorialById(@PathVariable("id") long id) {
        try {
            Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

            if (tutorialData.isPresent()) {
                return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, tutorialData);
            } else {
                throw new Exception("NOT_FOUND");
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @PostMapping("/tutorials")
    public ResponseEntity<Object> createTutorial(@RequestBody Tutorial tutorial) {
        try {
            Tutorial _tutorial = tutorialRepository.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));

            return ResponseHandler.generateResponse("SUCCESS", HttpStatus.CREATED, _tutorial);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("tutorials/{id}")
    public ResponseEntity<Object> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
        try {
            Optional<Tutorial> tutorialData = tutorialRepository.findById(id);
            Tutorial _tutorial = tutorialData.get();

            _tutorial.setTitle(tutorial.getTitle());
            _tutorial.setDescription(tutorial.getDescription());

            if (tutorial.getPublished() == null) {
                _tutorial.setPublished(false);
            } else {
                _tutorial.setPublished(tutorial.getPublished());
            }

            Tutorial updatedTutorial = tutorialRepository.save(_tutorial);

            return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, updatedTutorial);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("NOT_FOUND", HttpStatus.NOT_FOUND, null);
        }
    }

    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<Object> deleteTutorial(@PathVariable("id") long id) {
        try {
            Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

            if(tutorialData.isPresent()) {
                tutorialRepository.deleteById(id);
                return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, null);
            } else {
                throw new Exception("NOT_FOUND");
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @DeleteMapping("/tutorials")
    public ResponseEntity<Object> deleteAllTutorials() {
        try {
            tutorialRepository.deleteAll();

            return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, null);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
