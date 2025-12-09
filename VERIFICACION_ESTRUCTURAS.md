# Verificación de Estructuras de Datos y Algoritmos

## ✅ Estructuras de Datos Implementadas

### 1. **Arreglos (Arrays)**
- **Ubicación**: `CustomMap.java`
- **Uso**: Tabla hash `Node<K, V>[] table` para almacenar pares clave-valor
- **Ejemplo**: 
  ```java
  private Node<K, V>[] table;
  this.table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
  ```

### 2. **Lista Simple (ListaSimple)**
- **Ubicación**: `data_structs/ListaSimple.java`
- **Uso**: 
  - Almacenamiento de pacientes, médicos y citas
  - Retorno de datos desde repositorios JPA
  - Estructura base para otras operaciones
- **Ejemplos de uso**:
  - `PacienteService.getAll()` - Lista de pacientes
  - `MedicoService.getAll()` - Lista de médicos
  - `CitaService.getColaCitasPendientes()` - Conversión de Cola a ListaSimple
  - `BinaryTree.toList()` - Conversión de árbol a lista

### 3. **Pila (Stack)**
- **Ubicación**: `data_structs/Pila.java`
- **Uso**: Historial de citas (LIFO - Last In, First Out)
- **Implementación**: `CitaService.pilaHistorialCitas`
- **Métodos utilizados**:
  - `push()` - Agregar citas finalizadas al historial
  - `toList()` - Convertir pila a lista para serialización JSON
- **Endpoint**: `/api/cita/pila/historial`

### 4. **Cola (Queue)**
- **Ubicación**: `data_structs/Cola.java`
- **Uso**: Cola de citas pendientes (FIFO - First In, First Out)
- **Implementación**: `CitaService.colaCitasPendientes`
- **Métodos utilizados**:
  - `offer()` - Agregar citas a la cola
  - `poll()` - Atender siguiente cita
  - `peek()` - Ver siguiente cita sin removerla
- **Endpoint**: `/api/cita/cola/pendientes`

### 5. **Binary Tree (Árbol Binario)**
- **Ubicación**: `data_structs/BinaryTree.java` y `BinaryTreeNode.java`
- **Uso**: Búsqueda eficiente en el historial de citas
- **Implementación**: `CitaService.arbolBusquedaHistorial`
- **Funcionalidades**:
  - Búsqueda por ID de cita
  - Búsqueda por nombre de paciente
  - Almacenamiento ordenado para búsquedas rápidas
- **Endpoints**:
  - `/api/cita/historial/buscar/{id}` - Buscar por ID
  - `/api/cita/historial/buscar/paciente?nombre={nombre}` - Buscar por paciente

## ✅ Algoritmos Implementados

### 6. **Recursividad**
- **Ubicación**: `BinaryTree.java`
- **Métodos recursivos**:
  - `insertRecursive()` - Inserción recursiva en el árbol
  - `searchRecursive()` - Búsqueda recursiva en el árbol
  - `inOrderTraversal()` - Recorrido in-order recursivo
  - `preOrderTraversal()` - Recorrido pre-order recursivo
  - `postOrderTraversal()` - Recorrido post-order recursivo
  - `findByIdRecursive()` - Búsqueda por ID recursiva
- **Ejemplo**:
  ```java
  private BinaryTreeNode<T> insertRecursive(BinaryTreeNode<T> node, T data) {
      if (node == null) {
          return new BinaryTreeNode<>(data);
      }
      int comparison = comparator.compare(data, node.getData());
      if (comparison < 0) {
          node.setLeft(insertRecursive(node.getLeft(), data));
      } else {
          node.setRight(insertRecursive(node.getRight(), data));
      }
      return node;
  }
  ```

### 7. **Merge Sort**
- **Ubicación**: `data_structs/MergeSort.java`
- **Uso**: Ordenamiento de pacientes por prioridad
- **Implementación**:
  - `PacienteService.getAllPrioridadAsc()` - Ordena pacientes por prioridad ascendente
  - `CitaService.atenderPacientePorPrioridad()` - Ordena pacientes antes de atender
- **Características**:
  - Divide y vencerás (divide la lista en mitades)
  - Llamadas recursivas para ordenar cada mitad
  - Mezcla (merge) de listas ordenadas
- **Endpoint**: `/api/paciente/prioridad/asc`

### 8. **Bubble Sort**
- **Ubicación**: `data_structs/BubbleSort.java` (NUEVO)
- **Uso**: Ordenamiento de médicos por nombre o especialidad
- **Implementación**:
  - `BubbleSort.sortByNombreAsc()` - Ordena médicos por nombre alfabéticamente
  - `BubbleSort.sortByEspecialidadAsc()` - Ordena médicos por especialidad
  - `MedicoService.getAllOrdenadosPorNombre()` - Endpoint que usa BubbleSort
- **Características**:
  - Compara elementos adyacentes
  - Intercambia si están en orden incorrecto
  - Optimizado con flag `swapped` para detenerse si ya está ordenado
- **Endpoint**: `/api/medico/ordenados/nombre` (puede agregarse al controlador)

## 📊 Resumen de Uso por Servicio

### CitaService
- ✅ **Cola**: `colaCitasPendientes` - Citas pendientes
- ✅ **Pila**: `pilaHistorialCitas` - Historial de citas
- ✅ **BinaryTree**: `arbolBusquedaHistorial` - Búsqueda en historial
- ✅ **ListaSimple**: Conversiones y almacenamiento temporal
- ✅ **MergeSort**: Ordenamiento de pacientes por prioridad

### PacienteService
- ✅ **ListaSimple**: Almacenamiento de pacientes
- ✅ **MergeSort**: Ordenamiento por prioridad

### MedicoService
- ✅ **ListaSimple**: Almacenamiento de médicos
- ✅ **BubbleSort**: Ordenamiento por nombre/especialidad

### CustomMap (Estructura personalizada)
- ✅ **Arreglos**: Tabla hash con `Node<K, V>[]`

## 🔍 Verificación Final

| Estructura/Algoritmo | Estado | Ubicación Principal |
|---------------------|--------|---------------------|
| Arreglos | ✅ | `CustomMap.java` |
| Lista Simple | ✅ | `ListaSimple.java` - Usado en todos los servicios |
| Pila | ✅ | `Pila.java` - `CitaService.pilaHistorialCitas` |
| Cola | ✅ | `Cola.java` - `CitaService.colaCitasPendientes` |
| Binary Tree | ✅ | `BinaryTree.java` - `CitaService.arbolBusquedaHistorial` |
| Recursividad | ✅ | `BinaryTree.java` - Múltiples métodos recursivos |
| Merge Sort | ✅ | `MergeSort.java` - Ordenamiento de pacientes |
| Bubble Sort | ✅ | `BubbleSort.java` - Ordenamiento de médicos |

## ✅ TODAS LAS ESTRUCTURAS Y ALGORITMOS ESTÁN IMPLEMENTADOS Y EN USO

