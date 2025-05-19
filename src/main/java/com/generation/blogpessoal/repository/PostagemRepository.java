package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long>{

	    // Consulta postagens cujo título contenha a palavra (ignorando maiúsculas/minúsculas)
	    // SELECT *FROM tb_postagens WHERE titulo LIKE "%?%"
		List<Postagem> findAllByTituloContainingIgnoreCase(String titulo);

}
