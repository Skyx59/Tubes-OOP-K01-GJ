    package model.order;

    public record FinalScoreDTO(
            int score,
            int success,
            int failed,
            boolean passed
    ) {}
