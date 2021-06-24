package br.com.alura.forum.controller;

import br.com.alura.forum.controller.request.AtualizacaoTopicoRequest;
import br.com.alura.forum.controller.request.TopicoRequest;
import br.com.alura.forum.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.dto.TopicoDto;
import br.com.alura.forum.model.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public List<TopicoDto> lista(String nomeCurso){
        if (nomeCurso == null){
            List<Topico> topicos = topicoRepository.findAll();
            return TopicoDto.convert(topicos);
        }else{
            List<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso);
            return TopicoDto.convert(topicos);
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoRequest topicoRequest,
                                                UriComponentsBuilder uriBuilder){
        Topico topico = topicoRequest.converter(cursoRepository);
        topicoRepository.save(topico);
        URI uri= uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id){
        Optional<Topico> topico = topicoRepository.findById(id);
        if(topico.isPresent()){
            return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoRequest request){
        Optional<Topico> optional = topicoRepository.findById(id);
            if(optional.isPresent()){
                Topico topico = request.atualizar(id, topicoRepository);
                return ResponseEntity.ok(new TopicoDto(topico));
            }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> remover(@PathVariable Long id){
        topicoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
