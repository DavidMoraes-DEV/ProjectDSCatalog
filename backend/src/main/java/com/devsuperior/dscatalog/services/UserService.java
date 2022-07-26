package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service 
public class UserService implements UserDetailsService {
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class); //Esse tipo de objeto LOGGER irá imprimir mensagens no console obedecendo o padrão de erro, auxilia no caso de dar algum erro será impresso no console a mensagem definida para ajudar a intender o que ocorreu
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired 
	private UserRepository repository;
	
	@Autowired 
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		
		Page<User> page = repository.findAll(pageable);
		
		return page.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {

		Optional<User> obj = repository.findById(id);
		User user = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found")); 
		
		return new UserDTO(user); //Passando apenas o user pois os perfis ROLE já irão vir vinculado a esse perfil por conta do comando FETCH colocado na associação ManyToMany da entidade USER
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		
		User entity = new User();
		copyDtoToEntity(dto, entity); //Utilizamos então um método auxiliar para realizar a cópia dos dados do produto para não utilizar vários SETs nesse método
		entity.setPassword(passwordEncoder.encode(dto.getPassword())); //Utilizando a dependencia do BCryptPasswordEncoder utilizando o método .encode() que é o encarregado de pegar uma string qualquer e transforma-la em um hash
		
		entity = repository.save(entity);
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity = repository.getOne(id); 
			copyDtoToEntity(dto, entity);
			
			entity = repository.save(entity);
			return new UserDTO(entity);
			
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found" + id);
		}
	}

	public void delete(Long id) {
		
		try {
			repository.deleteById(id);	
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity Violation");
		}
	}
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		
		//Não se copia o Id, porque não se troca ele na hora de ATUALIZAR e também não informa ele na hora de INSERIR, por isso o id não entra no método de copiar os dados
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
				
		entity.getRoles().clear();
		for(RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role); 
		}
	}

	//Pega o nome do usuário fornecido como parâmetro e retorna um objeto UserDetails dele. Esse método pode lançar uma exceção UsernameNotFoundException quando o username não for encontrado
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = repository.findByEmail(username);
		if (user == null) {
			logger.error("User not found: " + username); //Caso de erro será impresso no console a mensagem para auxiliar qual o email que não foi encontrado
			throw new UsernameNotFoundException("Email not Found");
		}
		logger.info("User found: " + username); //Se não der nenhum erro será impresso no console a mensagem que encontrou o email
		return user;
	}
}