package com.store.book.libraryservice.client;

import com.store.book.libraryservice.exception.BookNotFoundException;
import com.store.book.libraryservice.exception.ExceptionMessage;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RetrieveMessageErrorDecoder implements ErrorDecoder {

    private static final String DATE = "date";

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    @SuppressWarnings({"java:S2259", "java:S112"})
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message = null;
        try (InputStream body = response.body().asInputStream()) {
            message = new ExceptionMessage((String) response.headers().get(DATE).toArray()[0],
                    response.status(),
                    HttpStatus.resolve(response.status()).getReasonPhrase(),
                    IOUtils.toString(body, StandardCharsets.UTF_8),
                    response.request().url());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (response.status() == HttpStatus.NOT_FOUND.value()){
            throw new BookNotFoundException(message);
        }
        return errorDecoder.decode(methodKey, response);
    }
}
