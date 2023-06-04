package jass.security.service.interfaces;

import jass.security.model.RejectedMail;

public interface IRejectedMailService extends ICrudService<RejectedMail> {
    boolean isMailRejected(String email);
}
