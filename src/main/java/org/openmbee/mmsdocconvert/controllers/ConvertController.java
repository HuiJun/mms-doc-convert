package org.openmbee.mmsdocconvert.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.openmbee.mmsdocconvert.objects.ConvertRequest;
import org.openmbee.mmsdocconvert.services.ConvertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/convert")
public class ConvertController {
    private static final Logger logger = LoggerFactory.getLogger(ConvertController.class);

    protected final ConvertService convertService;

    public ConvertController(ConvertService convertService) {
        this.convertService = convertService;
    }

    @RequestMapping(value = "/**", method = RequestMethod.POST)
    @Operation(operationId = "convertRequest", summary = "Request document conversion", description = "Converts requested HTML and CSS into request output format")
    @ApiResponse(content = @Content(mediaType = "application/pdf"), description = "PDF File Response")
    @ApiResponse(content = @Content(mediaType = "application/latex"), description = "Latex File Response")
    @ApiResponse(content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"), description = "Word Document File Response")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public @ResponseBody HttpEntity<byte[]> index(@RequestBody ConvertRequest convertRequest) {
        logger.debug(convertRequest.getUser() + " " + convertRequest.getHtml());
        byte[] document = convertService.convert(convertRequest.getHtml(), convertRequest.getCss(), convertRequest.getFormat());
        String fileName = convertService.getFileName();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", convertRequest.getFormat().getFormatName()));
        header.set("Content-Disposition", "inline; filename=" + fileName);
        header.setContentLength(document.length);

        return new HttpEntity<>(document, header);
    }
}
