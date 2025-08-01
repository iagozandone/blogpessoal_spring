package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;
import com.generation.blogpessoal.repository.UsuarioRepository; // ✅ [LINHA NOVA] Import do UsuarioRepository

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

	@Autowired
	private PostagemRepository postagemRepository;

	@Autowired
	private TemaRepository temaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository; // ✅ [LINHA NOVA] Injeta o UsuarioRepository

	@GetMapping
	public ResponseEntity<List<Postagem>> getAll() {
		return ResponseEntity.ok(postagemRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id) {
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo) {
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}

	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {

		if (!temaRepository.existsById(postagem.getTema().getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe!", null);
		}

		if (!usuarioRepository.existsById(postagem.getUsuario().getId())) { // ✅ [LINHA NOVA] Validação do usuário
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Usuário não existe!", null); // ✅ [LINHA NOVA]
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
	}

	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {

		if (postagem.getId() == null)
			return ResponseEntity.badRequest().build();

		if (postagemRepository.existsById(postagem.getId())) {

			if (!temaRepository.existsById(postagem.getTema().getId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe!", null);
			}

			if (!usuarioRepository.existsById(postagem.getUsuario().getId())) { // ✅ [LINHA NOVA] Validação do usuário também no PUT
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Usuário não existe!", null); // ✅ [LINHA NOVA]
			}

			return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem));
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Optional<Postagem> postagem = postagemRepository.findById(id);

		if (postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		postagemRepository.deleteById(id);
	}
}
