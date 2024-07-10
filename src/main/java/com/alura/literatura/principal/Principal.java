package com.alura.literatura.principal;

import com.alura.literatura.model.AutorLibro;
import com.alura.literatura.model.Datos;
import com.alura.literatura.model.DatosLibros;
import com.alura.literatura.model.Libro;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Principal {
    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoAPI consumoapi;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private final LibroRepository libroRepository;
    private final ConvierteDatos conversor;
    private List <Libro> libros = new ArrayList<>();

    @Autowired
    public Principal(ConsumoAPI consumoapi, ConvierteDatos conversor, LibroRepository libroRepository) {

        this.consumoapi = consumoapi;
        this.conversor = conversor;
        this.libroRepository = libroRepository;
    }



    public void muestraElMenu(){

        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    1 - Buscar libro por titulo 
                    2 - Listar libro registrado
                    3 - Listar Autores registrados
                    4 - Listar autoros vivos en un año determinado
                    5 - Listar libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();


                    switch (opcion){
                        case 1:
                            buscarLibro();
                            break;
                        case 2:
                            MostrarLibros();
                            break;
                        case 3:
                            MostrarAutores();
                            break;
                        case 4:
                            MostrarAutoriesVivosporAño();
                            break;
                        case 5:
                            MostrarLibrosPorIdioma();
                            break;
                        case 0:
                            System.out.println("Cerrando Aplicacion...");
                            break;
                        default:
                            System.out.println("Opcion no valida.");



                    }

        }
    }

    private Datos getDatosLibro(){
        System.out.println("Escribre el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        String json = consumoapi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        System.out.println(json);
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;

    }

    private void ImprimirLibros(Libro libro){
        System.out.println("Titulo: " + libro.getTitulo());
        System.out.println("Autor: " );
        for (AutorLibro autor : libro.getAutores()) {
            System.out.print(autor.getNombre() + " ");
        }
        System.out.println();
        System.out.print("Idioma: ");
        for (String idioma : libro.getLibroIdiomas()) {
            System.out.print(idioma + " ");
        }
        System.out.println();
    }

    private void buscarLibro() {
        Datos datos = getDatosLibro();
        if (datos.resultados() != null && !datos.resultados().isEmpty()){
            DatosLibros datosLibros = datos.resultados().get(0);
            if (libroRepository.existsByTitulo(datosLibros.titulo())){
                System.out.println("El libro ya se ah registrado en la base de datos");
            } else {
                Libro libro = new Libro(datosLibros);
                libroRepository.save(libro);
                ImprimirLibros(libro);

            }
            } else {
            System.out.println("El libro no se ah encontrado en la base de datos");

        }

    }



    @Transactional
    private void MostrarLibrosPorIdioma() {
        System.out.println("Ingresa el idioma que deseas que buscar");
        System.out.println("es, en , fr ,pt");

        var codigoIdioma = teclado.nextLine();

        List<Libro> libross = libroRepository.findAlByIdiomaWithAutores(codigoIdioma);
        if (libross.isEmpty()){
            System.out.println("No se encuenta el idioma en los registros");
        } else {
            System.out.println("Titulo: " + libross.get(0).getTitulo());
            System.out.println("IDIOMAS LISTADOS:");
            libross.forEach(libro -> {
                System.out.println("AUTOR");
                libro.getAutores().forEach(autorLibro -> System.out.println(autorLibro.getNombre()+""));

            });

        }
    }

    private void MostrarAutoriesVivosporAño() {
        System.out.println("Ingresa el autor que deseas que buscar");
        int año = teclado.nextInt();

        List<AutorLibro> autores = libroRepository.findAllAutores();
        autores.stream()
                .filter(a -> a.getNacimiento() <= año && (a.getMuerte() == null || a.getMuerte() >= año))
                .map(AutorLibro::getNombre)
                .distinct()
                .forEach(n -> System.out.println(n));

                }

    @Transactional
    private void MostrarAutores() {
        List<AutorLibro> autores = libroRepository.findAllAutores();
        Set<AutorLibro> autorUnico = new HashSet<>(autores);
        autorUnico.forEach(autor -> {
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Año de nacimiento: " + autor.getNacimiento());
            System.out.println("Año de muerte: " + autor.getMuerte());
        });
    }

    @Transactional
    private void MostrarLibros() {
        List<Libro> librosAutor = libroRepository.findAllWithAutores();
        List<Libro> librosIdioma = libroRepository.finAllWithIdiomas();

        Map<Long, Libro> libroMap = new HashMap<>();
        for (Libro libro : librosAutor){
            libroMap.put(libro.getId(), libro);
        }

        for (Libro libro : librosIdioma){
            Libro libroRegistrado = libroMap.get(libro.getId());
            if (libroRegistrado != null){
                libroRegistrado.setLibroIdiomas(libro.getLibroIdiomas());
            } else {
                libroMap.put(libro.getId(), libro);
            }
        }

        libros= new ArrayList<>(libroMap.values());

        libros.stream().forEach(System.out::println);
    }



}


