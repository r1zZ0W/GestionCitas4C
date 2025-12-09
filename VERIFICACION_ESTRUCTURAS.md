# Verificación de Estructuras de Datos y Algoritmos
## Sistema de Gestión de Citas Médicas

---

## 📋 Índice
1. [Estructuras de Datos Implementadas](#estructuras-de-datos-implementadas)
2. [Algoritmos Implementados](#algoritmos-implementados)
3. [Flujo Completo del Sistema](#flujo-completo-del-sistema)
4. [Uso de Estructuras por Componente](#uso-de-estructuras-por-componente)
5. [Verificación Final](#verificación-final)

---

## ✅ Estructuras de Datos Implementadas

### 1. **Arreglos (Arrays)**
- **Ubicación**: `data_structs/CustomMap.java`
- **Uso**: Tabla hash `Node<K, V>[] table` para almacenar pares clave-valor
- **Implementación**: Estructura personalizada que extiende `AbstractMap`
- **Ejemplo de uso**:
  ```java
  private Node<K, V>[] table;
  this.table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
  ```
- **Dónde se usa**:
  - `CitaService.tareasProgramadas` - Almacena tareas programadas (ScheduledFuture) por ID de cita usando `CustomMap<Integer, ScheduledFuture<?>>`
  - Respuestas de servicios (CustomMap<String, Object>) para estructurar datos JSON
  - `PacienteService.getAllPrioridadAsc()` - Usa `CustomMap<Integer, Cita>` para asociar citas con pacientes

### 2. **Lista Simple (ListaSimple)**
- **Ubicación**: `data_structs/ListaSimple.java`
- **Tipo**: Lista enlazada simple genérica
- **Uso Principal**: 
  - Almacenamiento temporal de datos
  - Conversión entre estructuras
  - Retorno de datos desde repositorios JPA
  - Filtrado y procesamiento de datos
- **Ejemplos de uso**:
  - `PacienteService.getAll()` - Retorna `ListaSimple<Paciente>`
  - `MedicoService.getAll()` - Retorna `ListaSimple<Medico>`
  - `CitaService.getColaCitasPendientes()` - Convierte `Cola` a `ListaSimple` para JSON
  - `BinaryTree.toList()` - Convierte árbol a lista para recorrido
  - `PacienteService.getAllPrioridadAsc()` - Filtra y almacena pacientes disponibles usando `ListaSimple<Paciente>` y `ListaSimple<Integer>` para evitar duplicados
  - `CitaService.atenderPacientePorPrioridad()` - Almacena pacientes, médicos y citas disponibles
  - `CitaService.getAll()` - Retorna todas las citas como `ListaSimple<Cita>`

### 3. **Pila (Stack) - LIFO**
- **Ubicación**: `data_structs/Pila.java`
- **Tipo**: Pila genérica con nodos enlazados
- **Uso**: Historial de citas finalizadas (Last In, First Out)
- **Implementación**: `CitaService.pilaHistorialCitas`
- **Métodos utilizados**:
  - `push(Cita)` - Agregar citas finalizadas al historial
  - `toList()` - Convertir pila a ListaSimple para serialización JSON
  - `isEmpty()` - Verificar si el historial está vacío
  - `size()` - Obtener tamaño del historial
- **Flujo**:
  1. Cuando una cita se finaliza (estado 'F'), se hace `push()` a la pila
  2. La última cita finalizada queda en el tope (primera en aparecer)
  3. Al consultar el historial, se convierte a ListaSimple para mostrar
  4. Al iniciar el servicio, se cargan todas las citas finalizadas desde BD a la pila
- **Endpoint**: `GET /api/cita/pila/historial`

### 4. **Cola (Queue) - FIFO**
- **Ubicación**: `data_structs/Cola.java`
- **Tipo**: Cola genérica con nodos enlazados
- **Uso**: Cola de citas pendientes (First In, First Out)
- **Implementación**: `CitaService.colaCitasPendientes`
- **Métodos utilizados**:
  - `offer(Cita)` - Agregar citas a la cola (Programadas, Reagendadas)
  - `poll()` - Atender siguiente cita (remover y retornar)
  - `peek()` - Ver siguiente cita sin removerla
  - `size()` - Obtener tamaño de la cola
- **Flujo**:
  1. Al crear una cita con estado 'P' (Programada) o 'R' (Reagendada) → `offer()` a la cola
  2. Al reagendar una cita → `offer()` a la cola nuevamente (vuelve a la cola)
  3. Al atender siguiente cita → `poll()` de la cola
  4. Al iniciar el servicio, se cargan todas las citas activas desde BD a la cola
- **Endpoints**: 
  - `GET /api/cita/cola/pendientes` - Ver citas en cola
  - `POST /api/cita/cola/atender` - Atender siguiente cita

### 5. **Binary Tree (Árbol Binario)**
- **Ubicación**: `data_structs/BinaryTree.java` y `BinaryTreeNode.java`
- **Tipo**: Árbol binario de búsqueda genérico
- **Uso**: Búsqueda eficiente O(log n) en el historial de citas
- **Implementación**: `CitaService.arbolBusquedaHistorial`
- **Comparador**: Ordena por ID de cita (ascendente)
- **Métodos recursivos utilizados**:
  - `insertRecursive()` - Inserción recursiva en el árbol
  - `searchRecursive()` - Búsqueda recursiva por dato
  - `findByIdRecursive()` - Búsqueda recursiva por ID
  - `inOrderTraversal()` - Recorrido in-order recursivo
  - `preOrderTraversal()` - Recorrido pre-order recursivo
  - `postOrderTraversal()` - Recorrido post-order recursivo
- **Flujo**:
  1. Al finalizar una cita → `insert()` en el árbol
  2. Al buscar por ID → `findById()` en el árbol (O(log n))
  3. Al buscar por paciente → Recorre el árbol y filtra por nombre
  4. Al iniciar el servicio, se cargan todas las citas finalizadas desde BD al árbol
- **Endpoints**:
  - `GET /api/cita/historial/buscar/{id}` - Buscar cita por ID
  - `GET /api/cita/historial/buscar/paciente?nombre={nombre}` - Buscar por nombre de paciente

---

## ✅ Algoritmos Implementados

### 6. **Recursividad**
- **Ubicación**: `BinaryTree.java`
- **Métodos recursivos**:
  - `insertRecursive(BinaryTreeNode<T> node, T data)` - Inserta recursivamente comparando valores
  - `searchRecursive(BinaryTreeNode<T> node, T data)` - Busca recursivamente en el árbol
  - `findByIdRecursive(BinaryTreeNode<T> node, Integer id, Function<T, Integer> idGetter)` - Busca por ID recursivamente
  - `inOrderTraversal(BinaryTreeNode<T> node, ListaSimple<T> result)` - Recorre en orden (izq, raíz, der)
  - `preOrderTraversal(BinaryTreeNode<T> node, ListaSimple<T> result)` - Recorre pre-orden (raíz, izq, der)
  - `postOrderTraversal(BinaryTreeNode<T> node, ListaSimple<T> result)` - Recorre post-orden (izq, der, raíz)
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
- **Complejidad**: O(n log n)
- **Uso**: Ordenamiento de pacientes por prioridad (1=Alta, 2=Media, 3=Baja)
- **Implementación**:
  - `MergeSort.sortByPrioridadAsc(ListaSimple<Paciente>)` - Ordena ascendente
  - Divide la lista en mitades recursivamente
  - Mezcla las mitades ordenadas
- **Dónde se usa**:
  - `PacienteService.getAllPrioridadAsc()` - Ordena pacientes disponibles por prioridad
  - `CitaService.atenderPacientePorPrioridad()` - Ordena pacientes antes de atender
- **Flujo**:
  1. Obtiene lista de pacientes disponibles (con citas programadas)
  2. Aplica MergeSort para ordenar por prioridad (ascendente)
  3. Retorna lista ordenada (Alta primero, luego Media, luego Baja)
- **Endpoint**: `GET /api/paciente/prioridad/asc`

### 8. **Bubble Sort**
- **Ubicación**: `data_structs/BubbleSort.java`
- **Complejidad**: O(n²) en peor caso, O(n) si ya está ordenado
- **Uso**: Ordenamiento de médicos por nombre o especialidad
- **Implementación**:
  - `BubbleSort.sortByNombreAsc(ListaSimple<Medico>)` - Ordena por nombre alfabético
  - `BubbleSort.sortByEspecialidadAsc(ListaSimple<Medico>)` - Ordena por especialidad
- **Características**:
  - Compara elementos adyacentes
  - Intercambia si están en orden incorrecto
  - Optimizado con flag `swapped` para detenerse si ya está ordenado
- **Dónde se usa**:
  - `MedicoService.getAllOrdenadosPorNombre()` - Ordena médicos alfabéticamente
- **Endpoint**: `GET /api/medico/ordenados/nombre` (puede agregarse al controlador)

---

## 🔄 Flujo Completo del Sistema

### **1. Creación de Cita con Prioridad**
```
Usuario crea cita en formulario → Frontend (citas.js)
├─ Selecciona paciente, médico, fecha, hora, motivo
├─ Selecciona PRIORIDAD (1=Alta, 2=Media, 3=Baja) ← NUEVO
├─ Selecciona estado (P=Programada, R=Reagendada, etc.)
├─ Envía POST /api/cita
└─ Backend: CitaService.create()
   ├─ Valida datos (fecha, hora, paciente, médico)
   ├─ Asigna estado 'P' (Programada) si no se especifica
   ├─ ACTUALIZA prioridad del paciente en BD ← NUEVO
   ├─ Guarda cita en BD (citaRepository.save())
   └─ Si estado es 'P' o 'R' → colaCitasPendientes.offer(cita)
      └─ Usa estructura manual: Cola<Cita>
```

### **2. Lista de Prioridad y Atención (ACTUALIZADO)**
```
GET /api/paciente/prioridad/asc → PacienteService.getAllPrioridadAsc()
├─ Obtiene todas las citas de BD usando ListaSimple<Cita>
├─ Filtra citas activas para HOY (P, E, R) usando ListaSimple
├─ Excluye pacientes con citas finalizadas (F) para hoy
├─ Crea ListaSimple<Paciente> con pacientes de las citas
├─ Usa ListaSimple<Integer> para evitar duplicados
├─ Aplica MergeSort.sortByPrioridadAsc() → Ordena por prioridad
└─ Retorna lista ordenada (Alta → Media → Baja)
   └─ Solo pacientes con CITAS PROGRAMADAS para hoy
```

### **3. Atender Paciente por Prioridad (ACTUALIZADO)**
```
POST /api/cita/atender/prioridad → CitaService.atenderPacientePorPrioridad()
├─ Verifica médicos disponibles → ListaSimple<Medico>
├─ Obtiene CITAS PROGRAMADAS (P) o REAGENDADAS (R) para hoy
│  └─ Usa ListaSimple<Cita> para filtrar
├─ Obtiene pacientes de las citas → ListaSimple<Paciente>
├─ Usa CustomMap<Integer, Cita> para asociar cita con paciente
├─ Aplica MergeSort.sortByPrioridadAsc() → Ordena por prioridad
├─ Selecciona primer paciente (mayor prioridad)
├─ Obtiene la CITA EXISTENTE asociada (no crea nueva) ← CORREGIDO
├─ Asigna médico disponible
├─ Marca paciente: enAtencion = true
├─ ACTUALIZA cita existente a estado 'E' (En Atención) ← CORREGIDO
├─ Guarda en BD
├─ Programa tarea (6 segundos) usando ScheduledExecutorService
│  └─ Almacena tarea en CustomMap<Integer, ScheduledFuture<?>>
└─ Después de 6 segundos (automático):
   ├─ Cambia estado cita a 'F' (Finalizada)
   ├─ Libera médico (ocupado = false)
   ├─ Verifica si paciente tiene otras citas activas
   ├─ Si no tiene otras citas → enAtencion = false
   ├─ pilaHistorialCitas.push(cita) → Usa estructura manual: Pila
   └─ arbolBusquedaHistorial.insert(cita) → Usa estructura manual: BinaryTree
```

### **4. Reagendar Cita**
```
PUT /api/cita/{id} → CitaService.update()
├─ Actualiza cita en BD
├─ Si nuevo estado es 'R' (Reagendada):
│  └─ colaCitasPendientes.offer(cita) → Vuelve a la cola
└─ Si nuevo estado es 'P' (Programada):
   └─ colaCitasPendientes.offer(cita) → Agrega a la cola
```

### **5. Consultar Historial**
```
GET /api/cita/pila/historial → CitaService.getHistorialCitas()
├─ Obtiene citas de pilaHistorialCitas (estructura manual: Pila)
├─ Convierte a ListaSimple usando pilaHistorialCitas.toList()
└─ Retorna lista (última finalizada primero - LIFO)
```

### **6. Búsqueda en Historial**
```
GET /api/cita/historial/buscar/{id} → CitaService.buscarCitaEnHistorial()
└─ Usa arbolBusquedaHistorial.findById() → Búsqueda O(log n) recursiva

GET /api/cita/historial/buscar/paciente?nombre={nombre} → CitaService.buscarCitasPorPaciente()
├─ Convierte árbol a ListaSimple usando toList()
├─ Filtra por nombre de paciente
└─ Retorna lista de coincidencias
```

### **7. Actualización Automática de Tablas (NUEVO)**
```
Frontend: citas.js
├─ setInterval() cada 3 segundos
├─ Llama a listarCitas() → Actualiza tabla de citas
└─ Llama a cargarPacientesPrioridad() → Actualiza lista de prioridad
   └─ Las tablas se actualizan automáticamente sin recargar página
```

---

## 📊 Uso de Estructuras por Componente

### **CitaService**
- ✅ **Cola<Cita>** `colaCitasPendientes`
  - Almacena citas pendientes (P, R)
  - Métodos: `offer()`, `poll()`, `peek()`, `size()`
  - Se carga al iniciar el servicio desde BD
  
- ✅ **Pila<Cita>** `pilaHistorialCitas`
  - Almacena historial de citas finalizadas (LIFO)
  - Métodos: `push()`, `toList()`, `isEmpty()`, `size()`
  - Se carga al iniciar el servicio desde BD
  
- ✅ **BinaryTree<Cita>** `arbolBusquedaHistorial`
  - Búsqueda eficiente en historial
  - Métodos recursivos: `insert()`, `findById()`, `search()`, `toList()`
  - Se carga al iniciar el servicio desde BD
  
- ✅ **ListaSimple<Cita>** (temporal)
  - Conversión de Cola/Pila/BinaryTree a lista
  - Filtrado y procesamiento de datos
  - Retorno de datos al frontend
  
- ✅ **ListaSimple<Paciente>** (temporal)
  - Almacena pacientes disponibles
  - Se ordena con MergeSort
  - Filtrado de pacientes con citas activas
  
- ✅ **ListaSimple<Medico>** (temporal)
  - Almacena médicos disponibles
  - Filtrado de médicos no ocupados
  
- ✅ **ListaSimple<Integer>** (temporal)
  - Evita duplicados en listas
  - Usado en `PacienteService.getAllPrioridadAsc()`
  
- ✅ **CustomMap<Integer, ScheduledFuture<?>>** `tareasProgramadas`
  - Almacena tareas programadas por ID de cita
  - Usa arreglos internamente (Node<K, V>[])
  
- ✅ **CustomMap<Integer, Cita>** (temporal)
  - Asocia citas con pacientes por ID
  - Usado en `atenderPacientePorPrioridad()`

- ✅ **MergeSort**
  - Ordena pacientes por prioridad antes de atender
  - Usado en `atenderPacientePorPrioridad()`

### **PacienteService**
- ✅ **ListaSimple<Paciente>**
  - Retorna listas de pacientes
  - Filtra pacientes disponibles
  
- ✅ **ListaSimple<Integer>**
  - Evita duplicados en lista de prioridad
  
- ✅ **ListaSimple<Cita>** (temporal)
  - Filtra citas activas para obtener pacientes
  
- ✅ **CustomMap<Integer, Cita>** (temporal)
  - Asocia citas con pacientes
  
- ✅ **MergeSort**
  - Ordena pacientes por prioridad en `getAllPrioridadAsc()`

### **MedicoService**
- ✅ **ListaSimple<Medico>**
  - Retorna listas de médicos
  
- ✅ **BubbleSort**
  - Ordena médicos por nombre o especialidad

### **CustomMap (Estructura personalizada)**
- ✅ **Arreglos (Node<K, V>[])**
  - Tabla hash para almacenar pares clave-valor
  - Usado en respuestas de servicios y tareas programadas

---

## 🔍 Verificación Final

| Estructura/Algoritmo | Estado | Ubicación Principal | Uso en el Sistema |
|---------------------|--------|---------------------|-------------------|
| **Arreglos** | ✅ | `CustomMap.java` | Tabla hash para respuestas, tareas programadas y asociación citas-pacientes |
| **Lista Simple** | ✅ | `ListaSimple.java` | Almacenamiento temporal, conversiones, retorno de datos, filtrado |
| **Pila** | ✅ | `Pila.java` | Historial de citas (LIFO) - última finalizada primero |
| **Cola** | ✅ | `Cola.java` | Citas pendientes (FIFO) - primera en entrar, primera en salir |
| **Binary Tree** | ✅ | `BinaryTree.java` | Búsqueda eficiente en historial (O(log n)) |
| **Recursividad** | ✅ | `BinaryTree.java` | Inserción, búsqueda y recorridos recursivos |
| **Merge Sort** | ✅ | `MergeSort.java` | Ordenamiento de pacientes por prioridad (O(n log n)) |
| **Bubble Sort** | ✅ | `BubbleSort.java` | Ordenamiento de médicos por nombre/especialidad (O(n²)) |

---

## 🎯 Resumen del Flujo de Datos Actualizado

### **Flujo de Cita Completo (ACTUALIZADO):**
```
1. CREAR CITA CON PRIORIDAD
   ├─ Usuario selecciona prioridad en formulario
   ├─ Prioridad se asigna al paciente
   └─ Estado 'P' → Cola (FIFO)

2. LISTA DE PRIORIDAD (ACTUALIZADO)
   ├─ Obtiene CITAS PROGRAMADAS (P, R) para hoy
   ├─ Extrae pacientes de las citas
   ├─ Filtra pacientes sin citas finalizadas
   ├─ Usa ListaSimple para almacenar
   └─ MergeSort por prioridad → Ordena pacientes

3. ATENDER PACIENTE (ACTUALIZADO)
   ├─ Obtiene CITAS PROGRAMADAS para hoy
   ├─ Asocia citas con pacientes (CustomMap)
   ├─ MergeSort → Selecciona mayor prioridad
   ├─ USA CITA EXISTENTE (no crea nueva)
   ├─ Estado 'E' → En atención
   ├─ Tarea programada (6 seg) → CustomMap
   └─ Después de 6 segundos (automático):
      ├─ Estado 'F' → Pila (LIFO) + BinaryTree
      └─ Libera recursos

4. REAGENDAR
   └─ Estado 'R' → Vuelve a Cola (FIFO)

5. HISTORIAL
   ├─ Pila → Última finalizada primero
   └─ BinaryTree → Búsqueda eficiente

6. ACTUALIZACIÓN AUTOMÁTICA (NUEVO)
   └─ setInterval cada 3 seg → Actualiza tablas automáticamente
```

### **Estructuras en Acción:**
- **Cola**: Citas pendientes y reagendadas (FIFO) - Solo citas programadas
- **Pila**: Historial de citas finalizadas (LIFO) - Última atendida primero
- **BinaryTree**: Búsqueda rápida en historial (recursivo) - O(log n)
- **ListaSimple**: Manipulación y filtrado de datos - Usado en todas las operaciones
- **MergeSort**: Ordenamiento por prioridad - O(n log n)
- **BubbleSort**: Ordenamiento de médicos - O(n²)
- **CustomMap**: Respuestas estructuradas, tareas programadas y asociaciones - Usa arreglos

### **Características Clave del Sistema:**
1. **Prioridad en el formulario de citas**: La prioridad se asigna al crear la cita, no al crear el paciente
2. **Atender desde citas programadas**: Solo atiende pacientes que tienen citas programadas (P) o reagendadas (R) para hoy
3. **Uso de cita existente**: No crea nuevas citas al atender, actualiza la cita programada existente
4. **Filtrado inteligente**: Excluye pacientes con citas finalizadas (F) para hoy
5. **Actualización automática**: Las tablas se actualizan cada 3 segundos sin recargar la página
6. **Estructuras manuales en cada operación**: Todas las operaciones críticas usan estructuras personalizadas

---

## ✅ TODAS LAS ESTRUCTURAS Y ALGORITMOS ESTÁN IMPLEMENTADOS Y EN USO ACTIVO

**El sistema utiliza estructuras de datos manuales en cada operación crítica:**
- ✅ Gestión de citas pendientes (Cola) - Solo citas programadas
- ✅ Historial de citas (Pila) - Última finalizada primero
- ✅ Búsqueda eficiente (BinaryTree) - Recursivo O(log n)
- ✅ Ordenamiento por prioridad (MergeSort) - O(n log n)
- ✅ Ordenamiento de médicos (BubbleSort) - O(n²)
- ✅ Almacenamiento temporal (ListaSimple) - En todas las operaciones
- ✅ Respuestas estructuradas (CustomMap con arreglos) - Tareas y asociaciones
- ✅ Actualización automática (setInterval) - Frontend cada 3 segundos

**Mejoras Implementadas:**
- ✅ Prioridad asignada al crear cita (no al crear paciente)
- ✅ Atender solo pacientes con citas programadas
- ✅ Uso de citas existentes (no creación de nuevas)
- ✅ Filtrado que excluye pacientes ya atendidos
- ✅ Actualización automática de tablas
