package com.ssafy.picple.domain.background.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalFileService {

	private final FileUploadService fileUploadService;

}