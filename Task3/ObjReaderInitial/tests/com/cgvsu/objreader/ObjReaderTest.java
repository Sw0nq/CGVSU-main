package com.cgvsu.objreader;

import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class ObjReaderTest {

    @Test
    public void testParseVertex01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3f result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.03f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex02() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3f result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.10f);
        Assertions.assertFalse(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex03() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("ab", "o", "ba"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
            Assertions.assertTrue(false);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Failed to parse float value.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex04() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
            Assertions.assertTrue(false);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Too few vertex arguments.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex05() {
        // АГААА! Вот тест, который говорит, что у метода нет проверки на более, чем 3 числа
        // А такой случай лучше не игнорировать, а сообщать пользователю, что у него что-то не так

        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0", "4.0"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
            Assertions.assertTrue(false);
        } catch (ObjReaderException exception) {
            String expectedError = "";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }
    @Test
    public void testParseFaceWord01() {
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        ObjReader.parseFaceWord("1", vertexIndices, textureIndices, normalIndices, 15);
        Assertions.assertEquals(0, vertexIndices.get(0));
        Assertions.assertTrue(textureIndices.isEmpty());
        Assertions.assertTrue(normalIndices.isEmpty());
    }

    @Test
    public void testParseFaceWord02() {
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        ObjReader.parseFaceWord("1/2", vertexIndices, textureIndices, normalIndices, 20);
        Assertions.assertEquals(0, vertexIndices.get(0));
        Assertions.assertEquals(1, textureIndices.get(0));
        Assertions.assertTrue(normalIndices.isEmpty());
    }

    @Test
    public void testParseFaceWord03() {
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        ObjReader.parseFaceWord("1/2/3", vertexIndices, textureIndices, normalIndices, 25);
        Assertions.assertEquals(0, vertexIndices.get(0));
        Assertions.assertEquals(1, textureIndices.get(0));
        Assertions.assertEquals(2, normalIndices.get(0));
    }

    @Test
    public void testParseFaceWord04() {
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        Assertions.assertThrows(ObjReaderException.class, () ->
                ObjReader.parseFaceWord("1/abc/3", vertexIndices, textureIndices, normalIndices, 30));
    }

    @Test
    public void testParseFaceWord05() {
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        Assertions.assertThrows(ObjReaderException.class, () ->
                ObjReader.parseFaceWord("1/2/3/4", vertexIndices, textureIndices, normalIndices, 35));
    }

    @Test
    public void testParseFaceWord06() {
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        ObjReader.parseFaceWord("1//3", vertexIndices, textureIndices, normalIndices, 40);
        Assertions.assertEquals(0, vertexIndices.get(0));
        Assertions.assertTrue(textureIndices.isEmpty());
        Assertions.assertEquals(2, normalIndices.get(0));
    }
}