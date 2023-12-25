package com.cgvsu.objreader;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ObjReader {

	private static final String OBJ_VERTEX_TOKEN = "v";
	private static final String OBJ_TEXTURE_TOKEN = "vt";
	private static final String OBJ_NORMAL_TOKEN = "vn";
	private static final String OBJ_FACE_TOKEN = "f";

	public static Model read(String fileContent) {
		File file = new File(filePath);
		//Проверка существования файла
		if (!file.exists()) {
			throw new ObjReaderException("File does not exist: " + filePath);
		}
		//Проверки расширения файла
		if (!filePath.toLowerCase().endsWith(".obj")) {
			throw new ObjReaderException("Invalid file extension. Expected .obj: " + filePath);
		}

		Model result = new Model();

		int lineInd = 0;
		try (Scanner scanner = new Scanner(fileContent)) {
			while (scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				ArrayList<String> wordsInLine = new ArrayList<String>(Arrays.asList(line.split("\\s+")));
				if (wordsInLine.isEmpty()) {
					continue;
				}

				final String token = wordsInLine.get(0);
				wordsInLine.remove(0);

				++lineInd;
				switch (token) {
					// Для структур типа вершин методы написаны так, чтобы ничего не знать о внешней среде.
					// Они принимают только то, что им нужно для работы, а возвращают только то, что могут создать.
					// Исключение - индекс строки. Он прокидывается, чтобы выводить сообщение об ошибке.
					// Могло быть иначе. Например, метод parseVertex мог вместо возвращения вершины принимать вектор вершин
					// модели или сам класс модели, работать с ним.
					// Но такой подход может привести к большему количеству ошибок в коде. Например, в нем что-то может
					// тайно сделаться с классом модели.
					// А еще это портит читаемость
					// И не стоит забывать про тесты. Чем проще вам задать данные для теста, проверить, что метод рабочий,
					// тем лучше.
					case OBJ_VERTEX_TOKEN -> result.vertices.add(parseVertex(wordsInLine, lineInd));
					case OBJ_TEXTURE_TOKEN -> result.textureVertices.add(parseTextureVertex(wordsInLine, lineInd));
					case OBJ_NORMAL_TOKEN -> result.normals.add(parseNormal(wordsInLine, lineInd));
					case OBJ_FACE_TOKEN -> result.polygons.add(parseFace(wordsInLine, lineInd));
					default -> {
					}
				}
			}
		} catch (Exception e) {
			//Проверки ошибок чтения файла
			throw new ObjReaderException("Failed to read the file.", lineInd, e);
		}
		//Проверка наличия вершин, полигонов и других элементов
		if (result.vertices.isEmpty() || result.polygons.isEmpty()) {
			throw new ObjReaderException("No vertices or polygons found in the file.");
		}

		return result;
	}

	// Всем методам кроме основного я поставил модификатор доступа protected, чтобы обращаться к ним в тестах
	protected static Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		try {
			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));

		} catch (NumberFormatException e) {
			throw new ObjReaderException("Failed to parse float value.", lineInd);

		} catch (IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few vertex arguments.", lineInd);
		}
	}

	protected static Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		try {
			return new Vector2f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)));

		} catch (NumberFormatException e) {
			throw new ObjReaderException("Failed to parse float value.", lineInd);

		} catch (IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few texture vertex arguments.", lineInd);
		}
	}

	protected static Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		try {
			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));

		} catch (NumberFormatException e) {
			throw new ObjReaderException("Failed to parse float value.", lineInd);

		} catch (IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few normal arguments.", lineInd);
		}
	}

	protected static Polygon parseFace(
			final ArrayList<String> wordsInLineWithoutToken,
			int lineInd,
			int totalVertices) {
		ArrayList<Integer> onePolygonVertexIndices = new ArrayList<Integer>();
		ArrayList<Integer> onePolygonTextureVertexIndices = new ArrayList<Integer>();
		ArrayList<Integer> onePolygonNormalIndices = new ArrayList<Integer>();

		for (String s : wordsInLineWithoutToken) {
			parseFaceWord(s, onePolygonVertexIndices, onePolygonTextureVertexIndices, onePolygonNormalIndices, lineInd, totalVertices);
		}

		Polygon result = new Polygon();
		result.setVertexIndices(onePolygonVertexIndices);
		result.setTextureVertexIndices(onePolygonTextureVertexIndices);
		result.setNormalIndices(onePolygonNormalIndices);
		return result;
	}

	// Обратите внимание, что для чтения полигонов я выделил еще один вспомогательный метод.
	// Это бывает очень полезно и с точки зрения структурирования алгоритма в голове, и с точки зрения тестирования.
	// В радикальных случаях не бойтесь выносить в отдельные методы и тестировать код из одной-двух строчек.
	protected static void parseFaceWord(
			String wordInLine,
			List<Integer> onePolygonVertexIndices,
			List<Integer> onePolygonTextureVertexIndices,
			List<Integer> onePolygonNormalIndices,
			int lineInd,
			int totalVertices) {
		try {
			String[] wordIndices = wordInLine.split("/");
			switch (wordIndices.length) {
				case 1 -> onePolygonVertexIndices.add(parseIndex(wordIndices[0], totalVertices, lineInd));
				case 2 -> {
					onePolygonVertexIndices.add(parseIndex(wordIndices[0], totalVertices, lineInd));
					if (!wordIndices[1].isEmpty()) {
						onePolygonTextureVertexIndices.add(parseIndex(wordIndices[1], totalVertices, lineInd));
					}
				}
				case 3 -> {
					onePolygonVertexIndices.add(parseIndex(wordIndices[0], totalVertices, lineInd));
					onePolygonNormalIndices.add(parseIndex(wordIndices[2], totalVertices, lineInd));
					if (!wordIndices[1].isEmpty()) {
						onePolygonTextureVertexIndices.add(parseIndex(wordIndices[1], totalVertices, lineInd));
					}
				}
				default -> {
					throw new ObjReaderException("Invalid element size.", lineInd);
				}
			}
		} catch (NumberFormatException e) {
			throw new ObjReaderException("Failed to parse index value.", lineInd);
		} catch (IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few arguments.", lineInd);
		}
	}
	private static int parseIndex(String indexStr, int totalVertices, int lineInd) {
		try {
			int index = Integer.parseInt(indexStr);
			return (index < 0) ? totalVertices + index : index - 1;
		} catch (NumberFormatException e) {
			throw new ObjReaderException("Failed to parse index value.", lineInd);
		}
	}

}


