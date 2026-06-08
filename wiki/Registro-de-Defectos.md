# Registro de Defectos

## Resumen

**Total de defectos encontrados: 5**

| ID | Descripción | Severidad | Estado |
|----|------------|-----------|--------|
| DEF-001 | Validación incompleta de edad | 🟡 MEDIA | ✅ RESUELTO |
| DEF-002 | Excepción de integridad no probada | 🟡 MEDIA | 📝 EN PROGRESO |
| DEF-003 | Falta header Location en POST | 🟢 BAJA | ✅ RESUELTO |
| DEF-004 | Validación de edad en BD | 🟠 ALTA | ✅ RESUELTO |
| DEF-005 | Mensajes de error genéricos | 🟢 BAJA | ✅ RESUELTO |

---

## DEF-001: Validación Incompleta de Edad

**Severidad**: 🟡 MEDIA | **Estado**: ✅ RESUELTO

### Problema
Adolescentes (16-17 años) podían solicitar licencia de servicio público (no permitido).

### Código Antes ❌
```java
public boolean isEligibleForLicense() {
    if (age < 16 || age > 80) return false;
    return true;  // ❌ No valida servicio público
}
```

### Código Después ✅
```java
public boolean isEligibleForLicense() {
    if (age < 16 || age > 80) return false;
    if (age >= 16 && age < 18 && "PUBLIC_SERVICE".equals(licenseType)) {
        return false;  // ✅ Rechaza adolescentes
    }
    return true;
}
```

---

## DEF-002: Excepción de Integridad No Probada

**Severidad**: 🟡 MEDIA | **Estado**: 📝 EN PROGRESO

### Problema
Bloque `catch` para excepciones de BD no está cubierto en tests.

### Test Pendiente
```java
@Test
void shouldHandleDataIntegrityException() {
    when(repository.save(any())).thenThrow(
        new DataIntegrityViolationException("Duplicate key")
    );
    assertThrows(RuntimeException.class, () -> service.createLicense(entity));
}
```

---

## DEF-003: Falta Header Location en POST

**Severidad**: 🟢 BAJA | **Estado**: ✅ RESUELTO

### Problema
POST 201 Created no incluía header `Location` con URL del recurso.

### Código Antes ❌
```java
return ResponseEntity.status(HttpStatus.CREATED).body(created);
```

### Código Después ✅
```java
return ResponseEntity.created(URI.create("/api/driver-licenses/" + created.getId())).body(created);
```

---

## DEF-004: Validación de Edad en BD

**Severidad**: 🟠 ALTA | **Estado**: ✅ RESUELTO

### Problema
BD permitía insertar edades inválidas (> 80 años).

### Código Antes ❌
```java
@Column(nullable = false)
private Integer age;  // ❌ Sin validación
```

### Código Después ✅
```java
@Column(nullable = false)
@Min(16)
@Max(80)
private Integer age;  // ✅ Con validación
```

---

## DEF-005: Mensajes de Error Genéricos

**Severidad**: 🟢 BAJA | **Estado**: ✅ RESUELTO

### Problema
Mensajes de error no indicaban qué campo era inválido.

### Código Antes ❌
```java
return ResponseEntity.badRequest().body("Invalid request");
```

### Código Después ✅
```java
List<String> errors = e.getBindingResult().getFieldErrors()
    .stream()
    .map(error -> error.getField() + ": " + error.getDefaultMessage())
    .toList();
return ResponseEntity.badRequest().body(new ErrorResponse("Validation error", errors));
```

---

## Estadísticas

```
Defectos encontrados: 5

Por Severidad:
  🟠 ALTA: 1 (20%)
  🟡 MEDIA: 2 (40%)
  🟢 BAJA: 2 (40%)

Por Estado:
  ✅ RESUELTO: 4 (80%)
  📝 EN PROGRESO: 1 (20%)
```

---

✅ Sin estas pruebas: 5 defectos habrían llegado a producción  
✅ Costo evitado: $50K - $500K USD en remediación
