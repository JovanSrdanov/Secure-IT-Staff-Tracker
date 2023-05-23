package pkibackend.pkibackend.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.model.KeystoreRowInfo;
import pkibackend.pkibackend.repository.KeystoreRowInfoRepository;
import pkibackend.pkibackend.service.interfaces.IKeystoreRowInfoService;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class KeystoreRowInfoService implements IKeystoreRowInfoService {
    private final KeystoreRowInfoRepository _keystoreRowInfoRepository;

    @Autowired
    public KeystoreRowInfoService(KeystoreRowInfoRepository keystoreRowInfoRepository) {
        this._keystoreRowInfoRepository = keystoreRowInfoRepository;
    }

    @Override
    public Iterable<Certificate> findAll() {
        return null;
    }

    @Override
    public Certificate findById(UUID id) {
        return null;
    }

    @Override
    public Certificate save(Certificate entity) throws BadRequestException {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public Optional<KeystoreRowInfo> findByCertificateSerialNumber(BigInteger serialNumber) {
        return _keystoreRowInfoRepository.findByCertificateSerialNumber(serialNumber);
    }
}
