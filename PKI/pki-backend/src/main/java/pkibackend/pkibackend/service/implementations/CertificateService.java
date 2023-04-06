package pkibackend.pkibackend.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pkibackend.pkibackend.exceptions.BadRequestException;
import pkibackend.pkibackend.model.Account;
import pkibackend.pkibackend.model.Certificate;
import pkibackend.pkibackend.repository.CertificateRepository;
import pkibackend.pkibackend.service.interfaces.ICertificateService;

import java.util.Date;
import java.util.UUID;

@Service
@Primary
public class CertificateService implements ICertificateService {
    private final CertificateRepository _certificateRepository;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository) {
        _certificateRepository = certificateRepository;
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
    public void generateCertificate(Certificate certificate) {
        _certificateRepository.SaveCertificate(certificate, "");
    }
}
