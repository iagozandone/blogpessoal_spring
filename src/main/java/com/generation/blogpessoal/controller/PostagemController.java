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

import jakarta.validation.Valid;

@RestController // Indica que a classe é um controlador REST, responsável por responder requisições HTTP no estilo REST
@RequestMapping("/postagens") // Define o endpoint base da URL para todas as rotas dentro dessa classe (ex: /postagens)
@CrossOrigin(origins = "*", allowedHeaders = "*") // Permite requisições de qualquer origem e com qualquer header (útil para front-ends externos acessarem a API)
public class PostagemController {

    @Autowired // Injeta automaticamente uma instância do repositório (injeção de dependência)
    private PostagemRepository postagemRepository;

    @GetMapping // Mapeia requisições HTTP GET para este método (ex: GET /postagens)
    public ResponseEntity<List<Postagem>> getAll(){ // Define o método que retorna uma lista de todas as postagens com status HTTP
        // SELECT * FROM tb_postagens; → Comentário explicando que essa chamada faz uma busca de todos os registros no banco
        return ResponseEntity.ok(postagemRepository.findAll()); // Retorna o resultado da consulta (lista de postagens) com status 200 OK
    }
    
    @GetMapping("/{id}") // Mapeia requisições GET com um parâmetro de caminho (ex: /postagens/1)
    public ResponseEntity<Postagem> getById(@PathVariable Long id) { // Recebe o ID da URL como argumento
    	return postagemRepository.findById(id) // Busca a postagem no banco de dados pelo ID
            .map(resposta -> ResponseEntity.ok(resposta)) // Se encontrar, retorna status 200 OK com o objeto encontrado
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Se não encontrar, retorna status 404 Not Found
    }
    @GetMapping("/titulo/{titulo}") // Mapeia requisições GET para /postagens/titulo/{titulo}
    public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo) { // Recebe o valor do título pela URL 
    	return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo)); // Retorna a lista com status 200 OK
	}
    @PostMapping // Mapeia requisições HTTP POST para este método (ex: POST /postagens)
    public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem){ // Recebe um objeto JSON no corpo da requisição, converte para Postagem e valida automaticamente os campos com base nas anotações da classe Postagem
        
    	return ResponseEntity.status(HttpStatus.CREATED) // Retorna o status HTTP 201 Created (registro criado com sucesso)
                             .body(postagemRepository.save(postagem)); // Salva o objeto no banco de dados e retorna o próprio objeto no corpo da resposta
    }
    @PutMapping // Mapeia requisições HTTP PUT para este método (ex: PUT /postagens)
    public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) { // Recebe um objeto JSON no corpo da requisição, valida com base nas anotações da classe Postagem

        if (postagem.getId() == null) // Verifica se o ID é nulo. Se for, não pode atualizar.
            return ResponseEntity.badRequest().build(); // Retorna status 400 Bad Request (requisição malformada)

        if (postagemRepository.existsById(postagem.getId())) // Verifica se o ID existe no banco
            return ResponseEntity.status(HttpStatus.OK) // Retorna status 200 OK
                                 .body(postagemRepository.save(postagem)); // Atualiza a postagem no banco

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Se o ID não existir, retorna 404 Not Found
    }
    
    @DeleteMapping("/{id}") // Mapeia requisições HTTP DELETE para a URL /postagens/{id}, onde {id} é o ID da postagem a ser deletada
    @ResponseStatus(HttpStatus.NO_CONTENT) // Define que, se a exclusão for bem-sucedida, a resposta HTTP será 204 No Content (sem conteúdo)
    public void delete(@PathVariable Long id) { // O @PathVariable vincula o valor da URL (id) ao parâmetro do método

        Optional<Postagem> postagem = postagemRepository.findById(id); // Busca no banco uma postagem com o ID informado (SELECT * FROM tb_postagens WHERE id = ?)

        if (postagem.isEmpty()) // Verifica se o resultado da busca está vazio (ou seja, se não encontrou nenhuma postagem)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Lança uma exceção com status 404 Not Found, encerrando o método

        postagemRepository.deleteById(id); // Se a postagem existir, deleta do banco de dados usando o ID (DELETE FROM tb_postagens WHERE id = ?)

        // SQL equivalente: DELETE FROM tb_postagens WHERE id = ?;
    }
}
