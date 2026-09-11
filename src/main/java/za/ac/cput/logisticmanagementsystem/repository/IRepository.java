/*
 * 28/07/2026
 *IRepository.java
 *IRepository model interface in repository folder
 *Maghdie Petersen 230600204
 *  */
package za.ac.cput.logisticmanagementsystem.repository;

public interface IRepository<T , ID> {
    T create(T t);
    T read(ID id);
    T update(T t);
    boolean delete(ID id);
}
