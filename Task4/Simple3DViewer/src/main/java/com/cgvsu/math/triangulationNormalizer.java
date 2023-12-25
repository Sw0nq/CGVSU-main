package com.cgvsu.math;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;

public class triangulationNormalizer {
    public static void triangulateAndNormalize(Model mesh) {
        triangulate(mesh);
        computeNormals(mesh);
    }

    private static void triangulate(Model mesh) {
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        for (Polygon polygon : mesh.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() >= 3) {
                for (int i = 1; i < vertexIndices.size() - 1; i++) {
                    Polygon triangle = new Polygon();
                    ArrayList<Integer> triangleVertices = new ArrayList<>();
                    triangleVertices.add(vertexIndices.get(0));
                    triangleVertices.add(vertexIndices.get(i));
                    triangleVertices.add(vertexIndices.get(i + 1));
                    triangle.setVertexIndices(triangleVertices);
                    newPolygons.add(triangle);
                }
            }
        }

        mesh.polygons = newPolygons;
    }

    private static void computeNormals(Model mesh) {
        for (Polygon polygon : mesh.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() >= 3) {
                Vector3f normal = calculatePolygonNormal(mesh, polygon);

                for (int vertexIndex : vertexIndices) {
                    Vector3f vertexNormal = mesh.normals.get(vertexIndex);

                    // Убедимся, что нормали были инициализированы
                    if (vertexNormal == null) {
                        vertexNormal = new Vector3f(0, 0, 0);
                        mesh.normals.set(vertexIndex, vertexNormal);
                    }

                    vertexNormal.add(normal);
                }
            }
        }

        // Нормализация векторов нормалей
        for (int i = 0; i < mesh.normals.size(); i++) {
            Vector3f vertexNormal = mesh.normals.get(i);
            if (vertexNormal != null) {
                vertexNormal.normalize();
            }
        }
    }


    private static Vector3f calculatePolygonNormal(Model mesh, Polygon polygon) {
        ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

        if (vertexIndices.size() >= 3) {
            Vector3f v1 = mesh.vertices.get(vertexIndices.get(0));
            Vector3f v2 = mesh.vertices.get(vertexIndices.get(1));
            Vector3f v3 = mesh.vertices.get(vertexIndices.get(2));

            Vector3f side1 = new Vector3f(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
            Vector3f side2 = new Vector3f(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);

            Vector3f normal = new Vector3f();
            normal.cross(side1, side2);
            normal.normalize();

            return normal;
        }

        return new Vector3f(0, 0, 0);
    }
}
