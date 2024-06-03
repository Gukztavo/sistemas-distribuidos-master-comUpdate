package service;

import java.util.Arrays;
import java.util.List;

public class CompetenciaService {

    private static final List<String> COMPETENCIAS_FIXAS = Arrays.asList(
            "Python", "C#", "C++", "JS", "PHP", "Swift", "Java", "Go", "SQL",
            "Ruby", "HTML", "CSS", "NOSQL", "Flutter", "TypeScript", "Perl",
            "Cobol", "dotNet", "Kotlin", "Dart"
    );

    public static List<String> getCompetenciasFixas() {
        return COMPETENCIAS_FIXAS;
    }

    public static boolean isCompetenciaValida(String competencia) {
        return COMPETENCIAS_FIXAS.contains(competencia);
    }
}

