package com.example.websocketdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class IService<Model, DB extends JpaRepository<Model, Integer>> {

    @Autowired
    DB db;

    public void add(Model obj) {
        db.save(obj);
    }

    public void delete(Model obj) {
        db.delete(obj);
    }

    public void delete(int id) {
        db.deleteById(id);
    }

    public Model findOne(int id) {
        return db.getOne(id);
    }

    public List<Model> findAll() {
        return db.findAll();
    }

    public boolean exists(int id) {
        return db.existsById(id);
    }


}
