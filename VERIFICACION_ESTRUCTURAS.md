# Verificación de Estructuras de Datos y Algoritmos
## Sistema de Gestión de Citas Médicas

---

## 📋 Índice
1. [Estructuras de Datos Implementadas](#estructuras-de-datos-implementadas)
2. [Algoritmos Implementados](#algoritmos-implementados)
3. [Uso Detallado con Fragmentos de Código](#uso-detallado-con-fragmentos-de-código)
4. [Flujo Completo del Sistema](#flujo-completo-del-sistema)
5. [Verificación Final](#verificación-final)

---

## ✅ Estructuras de Datos Implementadas

### 1. **Arreglos (Arrays)**
- **Ubicación**: `data_structs/CustomMap.java`
- **Tipo**: Tabla hash con arreglo de nodos `Node<K, V>[]`
- **Implementación**: Estructura personalizada que extiende `AbstractMap`
- **Uso Principal**: Almacenamiento de pares clave-valor para respuestas JSON, tareas programadas y asociaciones

### 2. **Lista Simple (ListaSimple)**
- **Ubicación**: `data_structs/ListaSimple.java`
- **Tipo**: Lista enlazada simple genérica con nodos
- **Uso Principal**: 
  - Almacenamiento temporal de datos
  - Conversión entre estructuras (Cola, Pila, BinaryTree)
  - Retorno de datos desde repositorios JPA
  - Filtrado y procesamiento de datos
  - Serialización JSON

### 3. **Pila (Stack) - LIFO**
- **Ubicación**: `data_structs/Pila.java`
- **Tipo**: Pila genérica con nodos enlazados (Last In, First Out)
- **Uso**: Historial de citas finalizadas
- **Complejidad**: O(1) para push/pop, O(n) para toList()

### 4. **Cola (Queue) - FIFO**
- **Ubicación**: `data_structs/Cola.java`
- **Tipo**: Cola genérica con nodos enlazados (First In, First Out)
- **Uso**: Cola de citas pendientes
- **Complejidad**: O(1) para offer/poll, O(n) para toList()

### 5. **Binary Tree (Árbol Binario de Búsqueda)**
- **Ubicación**: `data_structs/BinaryTree.java` y `BinaryTreeNode.java`
- **Tipo**: Árbol binario de búsqueda genérico
- **Uso**: Búsqueda eficiente O(log n) en el historial de citas
- **Complejidad**: O(log n) para búsqueda, O(n) para recorrido completo

### 6. **CustomMap (Tabla Hash Personalizada)**
- **Ubicación**: `data_structs/CustomMap.java`
- **Tipo**: Implementación personalizada de Map usando arreglos
- **Uso**: Respuestas estructuradas, tareas programadas, asociaciones

---

## ✅ Algoritmos Implementados

### 7. **Recursividad**
- **Ubicación**: `BinaryTree.java`
- **Uso**: Inserción, búsqueda y recorridos en el árbol binario
- **Métodos recursivos**: insertRecursive, searchRecursive, findByIdRecursive, inOrderTraversal, preOrderTraversal, postOrderTraversal

### 8. **Merge Sort**
- **Ubicación**: `data_structs/MergeSort.java`
- **Complejidad**: O(n log n)
- **Uso**: Ordenamiento de pacientes por prioridad (1=Alta, 2=Media, 3=Baja)

### 9. **Bubble Sort**
- **Ubicación**: `data_structs/BubbleSort.java`
- **Complejidad**: O(n²) en peor caso, O(n) si ya está ordenado
- **Uso**: Ordenamiento de médicos por nombre o especialidad

---

## 📝 Uso Detallado con Fragmentos de Código

### **1. COLA (Queue) - Citas Pendientes**

**Archivo**: `CitaService.java`

**Declaración e inicialización**:
```java
private final Cola<Cita> colaCitasPendientes = new Cola<>();
```

**Carga inicial al iniciar el servicio**:
```java
private void cargarCitasPendientes() {
    for (Cita cita : citaRepository.findAll())
        if (cita.getEstado() == 'A' || cita.getEstado() == 'P')
            colaCitasPendientes.offer(cita);
}
```

**Agregar cita a la cola (FIFO)**:
```java
// Cuando se crea o reagenda una cita
if (estadoNuevo == 'R' && estadoAnterior != 'R') {
    colaCitasPendientes.offer(citaExistente);
    mapResponse.put("message", "Cita reagendada y agregada a la cola de pendientes");
}
```

**Atender siguiente cita (remover de la cola)**:
```java
if (colaCitasPendientes.isEmpty()) {
    mapResponse.put("error", "No hay citas pendientes para atender");
    return mapResponse;
}

Cita citaAtendida = colaCitasPendientes.poll(); // FIFO: primera en entrar, primera en salir
citaAtendida.setEstado('F'); // F = Finalizada
```

**Endpoint**: `GET /api/cita/cola/pendientes` - Ver citas en cola
**Endpoint**: `POST /api/cita/cola/atender` - Atender siguiente cita

**Explicación**: La cola implementa el principio FIFO (First In, First Out). Las citas se agregan con `offer()` y se atienden con `poll()`, garantizando que la primera cita agregada sea la primera en ser atendida.

---

### **2. PILA (Stack) - Historial de Citas**

**Archivo**: `CitaService.java`

**Declaración e inicialización**:
```java
private final Pila<Cita> pilaHistorialCitas = new Pila<>();
```

**Carga inicial al iniciar el servicio**:
```java
private void cargarHistorialCitas() {
    for (Cita cita : citaRepository.findAll()) {
        if (cita.getEstado() != null && cita.getEstado() == 'F') { // F = Finalizada
            pilaHistorialCitas.push(cita);
            arbolBusquedaHistorial.insert(cita); // Para búsqueda eficiente
        }
    }
}
```

**Agregar cita finalizada al historial (LIFO)**:
```java
// Cuando una cita se finaliza
citaAtendida.setEstado('F'); // F = Finalizada
citaRepository.save(citaAtendida);

// Agregar al historial (Pila) y al árbol de búsqueda
pilaHistorialCitas.push(citaAtendida);
arbolBusquedaHistorial.insert(citaAtendida);
```

**Obtener historial completo**:
```java
public CustomMap<String, Object> getHistorialCitas() {
    CustomMap<String, Object> mapResponse = new CustomMap<>();
    
    // Convertir pila a ListaSimple para serialización JSON
    ListaSimple<Cita> historialLista = pilaHistorialCitas.toList();
    
    mapResponse.put("historialCitas", historialLista);
    mapResponse.put("tamaño", pilaHistorialCitas.size());
    mapResponse.put("isEmpty", pilaHistorialCitas.isEmpty());
    
    return mapResponse;
}
```

**Endpoint**: `GET /api/cita/pila/historial`

**Explicación**: La pila implementa el principio LIFO (Last In, First Out). La última cita finalizada queda en el tope y es la primera en aparecer al consultar el historial. Esto permite ver las citas más recientes primero.

---

### **3. BINARY TREE (Árbol Binario) - Búsqueda Eficiente**

**Archivo**: `CitaService.java`

**Declaración e inicialización con comparador**:
```java
private final BinaryTree<Cita> arbolBusquedaHistorial; // Para búsqueda eficiente

public CitaService(...) {
    // Crear BinaryTree para búsqueda por ID (más eficiente)
    this.arbolBusquedaHistorial = new BinaryTree<>((c1, c2) -> {
        Integer id1 = c1.getId() != null ? c1.getId() : 0;
        Integer id2 = c2.getId() != null ? c2.getId() : 0;
        return id1.compareTo(id2);
    });
    
    cargarHistorialCitas(); // Carga citas al árbol
}
```

**Inserción de citas en el árbol**:
```java
private void cargarHistorialCitas() {
    for (Cita cita : citaRepository.findAll()) {
        if (cita.getEstado() != null && cita.getEstado() == 'F') {
            pilaHistorialCitas.push(cita);
            arbolBusquedaHistorial.insert(cita); // Inserción recursiva O(log n)
        }
    }
}
```

**Búsqueda por ID usando recursividad (O(log n))**:
```java
public CustomMap<String, Object> buscarCitaEnHistorial(Integer id) {
    CustomMap<String, Object> mapResponse = new CustomMap<>();
    
    // Sincronizar primero
    sincronizarHistorialConBD();
    
    // Buscar en el árbol (más eficiente que recorrer la pila)
    // Usa recursividad: findByIdRecursive()
    Cita citaEncontrada = arbolBusquedaHistorial.findById(id, Cita::getId);
    
    if (citaEncontrada == null) {
        mapResponse.put("error", "Cita no encontrada en el historial con ID: " + id);
        mapResponse.put("code", 404);
        return mapResponse;
    }
    
    mapResponse.put("cita", citaEncontrada);
    mapResponse.put("message", "Cita encontrada en el historial");
    mapResponse.put("code", 200);
    
    return mapResponse;
}
```

**Búsqueda por nombre de paciente usando el árbol**:
```java
public CustomMap<String, Object> buscarCitasPorPaciente(String nombrePaciente) {
    CustomMap<String, Object> mapResponse = new CustomMap<>();
    
    // Sincronizar primero
    sincronizarHistorialConBD();
    
    // Buscar en todas las citas del historial usando el árbol binario
    // El árbol binario ordena las citas por ID, luego filtramos por nombre
    ListaSimple<Cita> citasEncontradas = new ListaSimple<>();
    String nombreBusqueda = nombrePaciente.toLowerCase().trim();
    
    // Obtener todas las citas del árbol binario (más eficiente que recorrer la pila)
    // Usa recorrido in-order recursivo: inOrderTraversal()
    ListaSimple<Cita> todasLasCitas = arbolBusquedaHistorial.toList();
    
    // Filtrar por nombre de paciente
    for (int i = 0; i < todasLasCitas.size(); i++) {
        Cita cita = todasLasCitas.get(i);
        if (cita.getPaciente() != null) {
            String nombreCompleto = (cita.getPaciente().getNombre() + " " + 
                                     cita.getPaciente().getApellido()).toLowerCase();
            if (nombreCompleto.contains(nombreBusqueda)) {
                citasEncontradas.add(cita);
            }
        }
    }
    
    mapResponse.put("citasEncontradas", citasEncontradas);
    mapResponse.put("tamaño", citasEncontradas.size());
    mapResponse.put("terminoBusqueda", nombrePaciente);
    mapResponse.put("code", 200);
    
    return mapResponse;
}
```

**Método recursivo de búsqueda por ID (en BinaryTree.java)**:
```java
private T findByIdRecursive(BinaryTreeNode<T> node, Integer id, Function<T, Integer> idGetter) {
    if (node == null) {
        return null;
    }
    
    Integer currentId = idGetter.apply(node.getData());
    if (currentId != null && currentId.equals(id)) {
        return node.getData();
    }
    
    T leftResult = findByIdRecursive(node.getLeft(), id, idGetter);
    if (leftResult != null) {
        return leftResult;
    }
    
    return findByIdRecursive(node.getRight(), id, idGetter);
}
```

**Método recursivo de inserción (en BinaryTree.java)**:
```java
private BinaryTreeNode<T> insertRecursive(BinaryTreeNode<T> node, T data) {
    if (node == null) {
        return new BinaryTreeNode<>(data);
    }
    
    int comparison = comparator.compare(data, node.getData());
    if (comparison < 0) {
        node.setLeft(insertRecursive(node.getLeft(), data));
    } else if (comparison > 0) {
        node.setRight(insertRecursive(node.getRight(), data));
    } else {
        // Si es igual, lo insertamos a la derecha (permite duplicados)
        node.setRight(insertRecursive(node.getRight(), data));
    }
    
    return node;
}
```

**Método recursivo de recorrido in-order (en BinaryTree.java)**:
```java
private void inOrderTraversal(BinaryTreeNode<T> node, ListaSimple<T> result) {
    if (node != null) {
        inOrderTraversal(node.getLeft(), result);  // Recursión izquierda
        result.add(node.getData());                 // Procesar raíz
        inOrderTraversal(node.getRight(), result); // Recursión derecha
    }
}
```

**Endpoints**:
- `GET /api/cita/historial/buscar/{id}` - Buscar cita por ID (O(log n))
- `GET /api/cita/historial/buscar/paciente?nombre={nombre}` - Buscar por nombre de paciente

**Explicación**: El árbol binario permite búsquedas eficientes en O(log n) en lugar de O(n) que tomaría recorrer una lista. El árbol ordena las citas por ID, y utiliza recursividad para insertar, buscar y recorrer los nodos. Para búsquedas por nombre, se obtienen todas las citas del árbol usando recorrido in-order y luego se filtran.

**Uso en Frontend** (`historial.js`):
```javascript
async function buscarPorPaciente(nombre) {
    try {
        const URL = `http://localhost:8080/api/cita/historial/buscar/paciente?nombre=${encodeURIComponent(nombre)}`;
        
        const response = await fetch(URL, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        
        const data = await response.json();
        const citasEncontradas = data.citasEncontradas || [];
        
        // Mostrar resultados en la tabla
        for (let i = 0; i < citasEncontradas.length; i++) {
            const cita = citasEncontradas[i];
            // ... renderizar en tabla
        }
    } catch (error) {
        console.error('Error al buscar:', error);
    }
}
```

---

### **4. LISTA SIMPLE - Almacenamiento y Procesamiento**

**Archivo**: `PacienteService.java`, `CitaService.java`, `MedicoService.java`

**Uso en PacienteService - Lista de prioridad**:
```java
public CustomMap<String, Object> getAllPrioridadAsc() {
    CustomMap<String, Object> mapResponse = new CustomMap<>();
    
    // Obtener pacientes basándose en sus citas activas (P, E, R)
    ListaSimple<Paciente> pacientesDisponibles = new ListaSimple<>();
    ListaSimple<Integer> pacientesIdsAgregados = new ListaSimple<>(); // Para evitar duplicados
    LocalDate fechaHoy = java.time.LocalDate.now();
    
    // Obtener todas las citas activas para hoy
    for (Cita cita : citaRepository.findAll()) {
        if (cita.getPaciente() != null && 
            cita.getFecha() != null &&
            cita.getFecha().equals(fechaHoy) &&
            (cita.getEstado() == 'P' || cita.getEstado() == 'E' || cita.getEstado() == 'R')) {
            
            Paciente paciente = cita.getPaciente();
            Integer pacienteId = paciente.getId();
            
            // Verificar que no haya sido agregado ya
            boolean yaAgregado = false;
            for (int i = 0; i < pacientesIdsAgregados.size(); i++) {
                if (pacientesIdsAgregados.get(i).equals(pacienteId)) {
                    yaAgregado = true;
                    break;
                }
            }
            
            if (!yaAgregado) {
                pacientesDisponibles.add(paciente);
                pacientesIdsAgregados.add(pacienteId);
            }
        }
    }
    
    // Ordenar por prioridad usando MergeSort
    ListaSimple<Paciente> listaPrioridad = MergeSort.sortByPrioridadAsc(pacientesDisponibles);
    
    mapResponse.put("listPacientes", listaPrioridad);
    mapResponse.put("code", 200);
    
    return mapResponse;
}
```

**Uso en CitaService - Conversión de estructuras**:
```java
public CustomMap<String, Object> getAll() {
    CustomMap<String, Object> mapResponse = new CustomMap<>();
    
    // Usar ListaSimple para la serialización JSON
    ListaSimple<Cita> listaCitas = new ListaSimple<>();
    listaCitas.addAll(citaRepository.findAll());
    mapResponse.put("listCitas", listaCitas);
    return mapResponse;
}
```

**Uso en repositorios JPA**:
```java
// PacienteRepository.java
@Query("SELECT p FROM Paciente p" +
        " WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR" +
        " LOWER(p.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
ListaSimple<Paciente> buscarPorNombreOApellido(@Param("termino") String termino);
```

**Explicación**: ListaSimple es una lista enlazada simple genérica que se usa extensivamente para almacenamiento temporal, conversión entre estructuras (Cola→Lista, Pila→Lista, BinaryTree→Lista), retorno de datos desde repositorios, y filtrado de datos. Es compatible con serialización JSON gracias a serializadores personalizados.

---

### **5. MERGE SORT - Ordenamiento por Prioridad**

**Archivo**: `MergeSort.java`, usado en `PacienteService.java`

**Implementación del algoritmo**:
```java
public static ListaSimple<Paciente> sortByPrioridadAsc(ListaSimple<Paciente> lista) {
    if (lista == null || lista.size() <= 1) {
        return lista;
    }
    
    // Dividir en dos mitades
    int medio = lista.size() / 2;
    ListaSimple<Paciente> izquierda = new ListaSimple<>();
    ListaSimple<Paciente> derecha = new ListaSimple<>();
    
    for (int i = 0; i < medio; i++) {
        izquierda.add(lista.get(i));
    }
    for (int i = medio; i < lista.size(); i++) {
        derecha.add(lista.get(i));
    }
    
    // Recursión: ordenar cada mitad
    izquierda = sortByPrioridadAsc(izquierda);
    derecha = sortByPrioridadAsc(derecha);
    
    // Mezclar las mitades ordenadas
    return merge(izquierda, derecha);
}

private static ListaSimple<Paciente> merge(ListaSimple<Paciente> izquierda, 
                                          ListaSimple<Paciente> derecha) {
    ListaSimple<Paciente> resultado = new ListaSimple<>();
    int i = 0, j = 0;
    
    while (i < izquierda.size() && j < derecha.size()) {
        int prioridadIzq = izquierda.get(i).getPrioridad() != null ? 
                           izquierda.get(i).getPrioridad() : 3;
        int prioridadDer = derecha.get(j).getPrioridad() != null ? 
                           derecha.get(j).getPrioridad() : 3;
        
        if (prioridadIzq <= prioridadDer) {
            resultado.add(izquierda.get(i));
            i++;
        } else {
            resultado.add(derecha.get(j));
            j++;
        }
    }
    
    // Agregar elementos restantes
    while (i < izquierda.size()) {
        resultado.add(izquierda.get(i));
        i++;
    }
    while (j < derecha.size()) {
        resultado.add(derecha.get(j));
        j++;
    }
    
    return resultado;
}
```

**Uso en PacienteService**:
```java
// Ordenar por prioridad usando MergeSort
ListaSimple<Paciente> listaPrioridad = MergeSort.sortByPrioridadAsc(pacientesDisponibles);
```

**Endpoint**: `GET /api/paciente/prioridad/asc`

**Explicación**: Merge Sort es un algoritmo de ordenamiento divide y vencerás con complejidad O(n log n). Divide la lista en mitades recursivamente, ordena cada mitad, y luego las mezcla. Se usa para ordenar pacientes por prioridad (1=Alta, 2=Media, 3=Baja), garantizando que los pacientes con mayor prioridad aparezcan primero.

---

### **6. BUBBLE SORT - Ordenamiento de Médicos**

**Archivo**: `BubbleSort.java`, usado en `MedicoService.java`

**Implementación del algoritmo**:
```java
public static ListaSimple<Medico> sortByNombreAsc(ListaSimple<Medico> lista) {
    if (lista == null || lista.size() <= 1) {
        return lista;
    }
    
    ListaSimple<Medico> resultado = new ListaSimple<>();
    resultado.addAll(lista);
    
    int n = resultado.size();
    boolean swapped;
    
    for (int i = 0; i < n - 1; i++) {
        swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            String nombre1 = resultado.get(j).getNombre() != null ? 
                            resultado.get(j).getNombre().toLowerCase() : "";
            String nombre2 = resultado.get(j + 1).getNombre() != null ? 
                            resultado.get(j + 1).getNombre().toLowerCase() : "";
            
            if (nombre1.compareTo(nombre2) > 0) {
                // Intercambiar
                Medico temp = resultado.get(j);
                resultado.set(j, resultado.get(j + 1));
                resultado.set(j + 1, temp);
                swapped = true;
            }
        }
        
        // Optimización: si no hubo intercambios, la lista ya está ordenada
        if (!swapped) {
            break;
        }
    }
    
    return resultado;
}
```

**Explicación**: Bubble Sort compara elementos adyacentes e intercambia si están en orden incorrecto. Tiene complejidad O(n²) en el peor caso, pero O(n) si la lista ya está ordenada gracias a la optimización con la bandera `swapped`. Se usa para ordenar médicos alfabéticamente por nombre o especialidad.

---

### **7. CUSTOMMAP - Tabla Hash Personalizada**

**Archivo**: `CustomMap.java`

**Uso para tareas programadas**:
```java
// En CitaService.java
private final CustomMap<Integer, ScheduledFuture<?>> tareasProgramadas = new CustomMap<>();

// Almacenar tarea programada
ScheduledFuture<?> tarea = scheduler.schedule(() -> {
    // ... lógica de finalización automática
}, 6, TimeUnit.SECONDS);
tareasProgramadas.put(citaId, tarea);

// Cancelar tarea si se elimina la cita
ScheduledFuture<?> tarea = tareasProgramadas.get(id);
if (tarea != null && !tarea.isDone()) {
    tarea.cancel(false);
    tareasProgramadas.remove(id);
}
```

**Uso para respuestas estructuradas**:
```java
// En todos los servicios
CustomMap<String, Object> mapResponse = new CustomMap<>();
mapResponse.put("message", "Operación exitosa");
mapResponse.put("listPacientes", listaPacientes);
mapResponse.put("code", 200);
return mapResponse;
```

**Explicación**: CustomMap es una implementación personalizada de Map que usa arreglos internamente (`Node<K, V>[] table`) para crear una tabla hash. Se usa para almacenar tareas programadas por ID de cita y para estructurar respuestas JSON de manera consistente.

---

## 🔄 Flujo Completo del Sistema

### **1. Creación de Cita**
```
Usuario crea cita → Frontend (citas.js)
├─ POST /api/cita
└─ Backend: CitaService.create()
   ├─ Valida datos
   ├─ Guarda en BD
   └─ Si estado es 'P' o 'R' → colaCitasPendientes.offer(cita) [COLA]
```

### **2. Atender Cita desde Cola**
```
POST /api/cita/cola/atender → CitaService.atenderSiguiente()
├─ colaCitasPendientes.poll() [COLA - FIFO]
├─ Cambia estado a 'F'
├─ pilaHistorialCitas.push(cita) [PILA - LIFO]
└─ arbolBusquedaHistorial.insert(cita) [BINARY TREE]
```

### **3. Lista de Pacientes por Prioridad**
```
GET /api/paciente/prioridad/asc → PacienteService.getAllPrioridadAsc()
├─ Filtra citas activas usando ListaSimple<Cita>
├─ Extrae pacientes usando ListaSimple<Paciente>
├─ Evita duplicados con ListaSimple<Integer>
└─ MergeSort.sortByPrioridadAsc() [MERGE SORT - O(n log n)]
```

### **4. Búsqueda en Historial por ID**
```
GET /api/cita/historial/buscar/{id} → CitaService.buscarCitaEnHistorial()
└─ arbolBusquedaHistorial.findById(id) [BINARY TREE - O(log n) recursivo]
```

### **5. Búsqueda en Historial por Nombre**
```
GET /api/cita/historial/buscar/paciente?nombre={nombre}
→ CitaService.buscarCitasPorPaciente()
├─ arbolBusquedaHistorial.toList() [BINARY TREE - recorrido in-order recursivo]
├─ Convierte a ListaSimple<Cita>
└─ Filtra por nombre de paciente
```

### **6. Consultar Historial Completo**
```
GET /api/cita/pila/historial → CitaService.getHistorialCitas()
├─ pilaHistorialCitas.toList() [PILA - convierte a ListaSimple]
└─ Retorna lista (última finalizada primero - LIFO)
```

---

## 🔍 Verificación Final

| Estructura/Algoritmo | Estado | Ubicación Principal | Complejidad | Uso en el Sistema |
|---------------------|--------|---------------------|-------------|-------------------|
| **Arreglos** | ✅ | `CustomMap.java` | O(1) promedio | Tabla hash para respuestas, tareas programadas |
| **Lista Simple** | ✅ | `ListaSimple.java` | O(n) búsqueda | Almacenamiento temporal, conversiones, retorno de datos |
| **Pila** | ✅ | `Pila.java` | O(1) push/pop | Historial de citas (LIFO) |
| **Cola** | ✅ | `Cola.java` | O(1) offer/poll | Citas pendientes (FIFO) |
| **Binary Tree** | ✅ | `BinaryTree.java` | O(log n) búsqueda | Búsqueda eficiente en historial |
| **Recursividad** | ✅ | `BinaryTree.java` | - | Inserción, búsqueda y recorridos |
| **Merge Sort** | ✅ | `MergeSort.java` | O(n log n) | Ordenamiento de pacientes por prioridad |
| **Bubble Sort** | ✅ | `BubbleSort.java` | O(n²) | Ordenamiento de médicos |
| **CustomMap** | ✅ | `CustomMap.java` | O(1) promedio | Respuestas estructuradas, tareas programadas |

---

## 🎯 Resumen del Flujo de Datos

### **Estructuras en Acción:**
- **Cola (FIFO)**: Citas pendientes - primera en entrar, primera en salir
- **Pila (LIFO)**: Historial de citas - última finalizada primero
- **BinaryTree (O(log n))**: Búsqueda eficiente con recursividad
- **ListaSimple**: Manipulación y filtrado de datos en todas las operaciones
- **MergeSort (O(n log n))**: Ordenamiento por prioridad
- **BubbleSort (O(n²))**: Ordenamiento de médicos
- **CustomMap**: Respuestas estructuradas y tareas programadas

### **Características Clave:**
1. ✅ **Cola manual** para gestión de citas pendientes (FIFO)
2. ✅ **Pila manual** para historial de citas (LIFO)
3. ✅ **Árbol binario manual** con recursividad para búsqueda eficiente O(log n)
4. ✅ **ListaSimple manual** para todas las operaciones de datos
5. ✅ **MergeSort manual** para ordenamiento por prioridad O(n log n)
6. ✅ **BubbleSort manual** para ordenamiento de médicos O(n²)
7. ✅ **CustomMap manual** con arreglos para respuestas y tareas
8. ✅ **Búsqueda en historial** usando árbol binario desde el frontend

---

## ✅ TODAS LAS ESTRUCTURAS Y ALGORITMOS ESTÁN IMPLEMENTADOS Y EN USO ACTIVO

**El sistema utiliza estructuras de datos manuales en cada operación crítica:**
- ✅ Gestión de citas pendientes (Cola - FIFO)
- ✅ Historial de citas (Pila - LIFO)
- ✅ Búsqueda eficiente (BinaryTree - O(log n) con recursividad)
- ✅ Ordenamiento por prioridad (MergeSort - O(n log n))
- ✅ Ordenamiento de médicos (BubbleSort - O(n²))
- ✅ Almacenamiento temporal (ListaSimple - en todas las operaciones)
- ✅ Respuestas estructuradas (CustomMap con arreglos)
- ✅ Búsqueda en historial desde frontend usando árbol binario
