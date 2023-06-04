package jass.security.service.implementations;

import jass.security.model.Address;
import jass.security.repository.IAddressRepository;
import jass.security.service.interfaces.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class AddressService implements IAddressService {
    private final IAddressRepository addressRepository;

    @Autowired
    public AddressService(IAddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public Address findById(UUID id) {
        return null;
    }

    @Override
    public Address save(Address entity) {
        return addressRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        addressRepository.deleteById(id);
    }
}
