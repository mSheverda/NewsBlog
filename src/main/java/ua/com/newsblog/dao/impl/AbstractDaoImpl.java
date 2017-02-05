package ua.com.newsblog.dao.impl;

import ua.com.newsblog.dao.AbstractDao;
import ua.com.newsblog.model.Model;

import java.util.Collection;
import java.util.List;


public abstract class AbstractDaoImpl<T extends Model> implements AbstractDao<T> {

    private final MainRepository<T, Long> repository;

    public AbstractDaoImpl(MainRepository<T, Long> repository) {
        this.repository = repository;
    }

    @Override
    public void add(T model) {
        this.repository.save(model);
    }

    @Override
    public void add(Collection<T> models) {
        this.repository.save(models);
    }

    @Override
    public void update(T model) {
        this.repository.save(model);
    }

    @Override
    public T get(Long id) {
        return this.repository.findOne(id);
    }

    @Override
    public List<T> getAll() {
        return this.repository.findAll();
    }

    @Override
    public void remove(T model) {
        this.repository.delete(model);
    }

    @Override
    public void remove(Long id) {
        this.repository.delete(id);
    }

    @Override
    public void remove(Collection<T> models) {
        this.repository.delete(models);
    }

    @Override
    public void removeAll() {
        this.repository.deleteAll();
    }
/*
    @Override
    public List<T> search(String pattern) {
        return this.repository.search(pattern) ;
    }
    */
}

