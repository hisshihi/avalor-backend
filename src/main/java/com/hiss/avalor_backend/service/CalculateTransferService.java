package com.hiss.avalor_backend.service;

import java.util.Map;

public interface CalculateTransferService {

    Map<String, Object> findTransfer(Long placeFrom, Long placeTo);

}
