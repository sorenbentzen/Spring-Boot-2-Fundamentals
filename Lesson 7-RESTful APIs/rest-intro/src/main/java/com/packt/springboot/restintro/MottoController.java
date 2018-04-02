package com.packt.springboot.restintro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/mottos")
public class MottoController {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Message {
        private String message;
    }

    private List<Message> motto = new ArrayList<>();

    /** Append to list of mottos, but only if the motto is unique */
    @PostMapping
    public ResponseEntity<Message> uniqueAppend(@RequestBody Message message, UriComponentsBuilder uriBuilder) {
        // Careful, this lookup is O(n)
        if (motto.contains(message)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            motto.add(message);
            URI location = uriBuilder.path("/api/mottos/{id}").build(motto.size());
            return ResponseEntity
                    .created(location)
                    .header("X-Copyright", "Packt 2018")
                    .body(new Message("Accepted #" + motto.size()));
        }
    }

    @GetMapping("/{id}")
    public Message retrieveById(@PathVariable int id) {
        return motto.get(id - 1);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Message outOfBounds(IndexOutOfBoundsException e) {
        return new Message(e.getMessage());
    }

}
