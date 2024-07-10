package com.alura.literatura.repository;

import com.alura.literatura.model.AutorLibro;
import com.alura.literatura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByTitulo(String titulo);
    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autores ")
    List<Libro> findAllWithAutores();


    @Query("SELECT DISTINCT a FROM AutorLibro a")
    List<AutorLibro> findAllAutores();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.libroIdiomas")
    List<Libro> finAllWithIdiomas();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autores WHERE :idioma MEMBER OF l.libroIdiomas")
    List<Libro> findAlByIdiomaWithAutores(String idioma);


}
