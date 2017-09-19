package by.kraskovski.pms.service.impl;

import by.kraskovski.pms.domain.model.Authority;
import by.kraskovski.pms.domain.enums.AuthorityEnum;
import by.kraskovski.pms.repository.AuthorityRepository;
import by.kraskovski.pms.service.AuthorityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor=@__(@Autowired))
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    public Authority create(final Authority object) {
        return authorityRepository.save(object);
    }

    @Override
    public Authority find(final String id) {
        return authorityRepository.findOne(id);
    }

    @Override
    public Authority findByName(final AuthorityEnum name) {
        return authorityRepository.findByAuthority(name);
    }

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    @Override
    public void delete(final String id) {
        authorityRepository.delete(id);
    }

    @Override
    public void deleteAll() {
        authorityRepository.deleteAll();
    }
}
