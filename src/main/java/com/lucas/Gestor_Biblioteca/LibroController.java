package com.lucas.Gestor_Biblioteca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

    @RestController
    @RequestMapping("/libros")
    public class LibroController {

        @Autowired
        private LibroRepository libroRepository;

        @GetMapping
        public List<Libro> listarTodos() {
            return libroRepository.findAll();
        }

        @GetMapping("/{id}")
        public Optional<Libro> buscarPorId(@PathVariable Long id) {
            return libroRepository.findById(id);
        }

        @PostMapping
        public Libro crear(@RequestBody Libro libro) {
            return libroRepository.save(libro);
        }

        @PutMapping("/{id}")
        public Libro actualizar(@PathVariable Long id, @RequestBody Libro libroActualizado) {
            libroActualizado.setId(id);
            return libroRepository.save(libroActualizado);
        }

        @DeleteMapping("/{id}")
        public void borrar(@PathVariable Long id) {
            libroRepository.deleteById(id);
        }
    }

