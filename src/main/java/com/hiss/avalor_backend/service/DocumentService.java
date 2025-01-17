package com.hiss.avalor_backend.service;

import com.hiss.avalor_backend.dto.DocumentContactDto;

import java.io.IOException;

public interface DocumentService {

    byte[] fillTemplateAndSendToFont(DocumentContactDto documentContactDto) throws IOException;

}
