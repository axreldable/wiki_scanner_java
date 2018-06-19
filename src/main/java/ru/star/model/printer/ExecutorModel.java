package ru.star.model.printer;

import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.ExecutorService;

@Builder
@Getter
public class ExecutorModel {
    private ExecutorService executor;
    private int threadsCount;
}
