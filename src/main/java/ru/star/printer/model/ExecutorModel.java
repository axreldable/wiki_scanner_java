package ru.star.printer.model;

import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.ExecutorService;

/**
 * Model for executor and amount of it's threads.
 */
@Builder
@Getter
public class ExecutorModel {
    private ExecutorService executor;
    private int threadsCount;
}
