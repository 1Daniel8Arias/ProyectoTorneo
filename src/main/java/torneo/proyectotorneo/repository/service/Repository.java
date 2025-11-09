package torneo.proyectotorneo.repository.service;

import torneo.proyectotorneo.exeptions.RepositoryException;

import java.util.ArrayList;

public interface Repository<T> {

    ArrayList<T> listarTodos() throws RepositoryException;

    T buscarPorId(int id) throws RepositoryException;

    void guardar(T t) throws RepositoryException;

    void actualizar(T t) throws RepositoryException;

    void eliminar(int t) throws RepositoryException;

}
