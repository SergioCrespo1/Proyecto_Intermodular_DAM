CREATE DATABASE IF NOT EXISTS smartfeed_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartfeed_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TRIGGER IF EXISTS tg_check_jaula_ocupada_insert;
DROP TRIGGER IF EXISTS tg_check_jaula_ocupada_update;
DROP TABLE IF EXISTS LINEA_CONSUMO_PIENSO;
DROP TABLE IF EXISTS REGISTRO_CONSUMO;
DROP TABLE IF EXISTS PROGRAMACION_TOMA;
DROP TABLE IF EXISTS REGISTRO_PESCA;
DROP TABLE IF EXISTS REGISTRO_MORTALIDAD;
DROP TABLE IF EXISTS INCIDENCIA_MANTENIMIENTO;
DROP TABLE IF EXISTS HISTORICO_TRASLADO;
DROP TABLE IF EXISTS LOTE_PECES;
DROP TABLE IF EXISTS LOTE_PIENSO;
DROP TABLE IF EXISTS PIENSO;
DROP TABLE IF EXISTS REGISTRO_TEMPERATURA;
DROP TABLE IF EXISTS JAULA;
DROP TABLE IF EXISTS ESPECIE;
DROP TABLE IF EXISTS USUARIO;

-- 1. USUARIO

CREATE TABLE USUARIO
(
    id_usuario      INT AUTO_INCREMENT,
    email           VARCHAR(100)                                          NOT NULL,
    password_hash   VARCHAR(255)                                          NOT NULL,
    nombre_completo VARCHAR(100)                                          NOT NULL,
    rol             ENUM ('ADMIN', 'TECNICO_OFICINA', 'MARINERO', 'BUZO') NOT NULL,
    activo          BOOLEAN                                               NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_email UNIQUE (email)
)
    ENGINE = InnoDB;

-- 2. ESPECIE

CREATE TABLE ESPECIE
(
    id_especie           INT AUTO_INCREMENT,
    nombre_comun         VARCHAR(50)   NOT NULL,
    nombre_cientifico    VARCHAR(100)  NOT NULL,
    fcr_referencia_medio DECIMAL(3, 2) NOT NULL,
    temp_min_operativa   DECIMAL(4, 2) NOT NULL,
    temp_max_operativa   DECIMAL(4, 2) NOT NULL,
    CONSTRAINT pk_especie PRIMARY KEY (id_especie),
    CONSTRAINT uq_especie_comun UNIQUE (nombre_comun),
    CONSTRAINT uq_especie_cientifico UNIQUE (nombre_cientifico)
)
    ENGINE = InnoDB;

-- 3. JAULA

CREATE TABLE JAULA
(
    id_jaula         INT AUTO_INCREMENT,
    codigo_jaula     VARCHAR(20)                               NOT NULL,
    diametro_m       DECIMAL(4, 2)                             NOT NULL,
    estado_operativo ENUM ('ACTIVA', 'VACIA', 'MANTENIMIENTO') NOT NULL DEFAULT 'VACIA',
    CONSTRAINT pk_jaula PRIMARY KEY (id_jaula),
    CONSTRAINT uq_jaula_codigo UNIQUE (codigo_jaula)
)
    ENGINE = InnoDB;

-- 4. REGISTRO_TEMPERATURA

CREATE TABLE REGISTRO_TEMPERATURA
(
    id_temperatura      INT AUTO_INCREMENT,
    fecha               DATE          NOT NULL,
    temperatura_c       DECIMAL(4, 2) NOT NULL,
    fecha_hora_registro DATETIME      NOT NULL,
    id_usuario          INT           NOT NULL,
    CONSTRAINT pk_temperatura PRIMARY KEY (id_temperatura),
    CONSTRAINT uq_temperatura_fecha UNIQUE (fecha),
    CONSTRAINT ck_temperatura_rango CHECK (temperatura_c BETWEEN 0.0 AND 40.0),
    CONSTRAINT fk_temperatura_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
)
    ENGINE = InnoDB;

-- 5. PIENSO

CREATE TABLE PIENSO
(
    id_pienso          INT AUTO_INCREMENT,
    nombre_comercial   VARCHAR(100)  NOT NULL,
    fabricante         VARCHAR(100)  NOT NULL,
    tamano_pellet_mm   DECIMAL(3, 1) NOT NULL,
    peso_pez_min_g     DECIMAL(6, 2) NOT NULL,
    peso_pez_max_g     DECIMAL(6, 2) NOT NULL,
    peso_saco_kg       DECIMAL(4, 1) NOT NULL DEFAULT 25.0,
    es_medicado        BOOLEAN       NOT NULL DEFAULT FALSE,
    medicamento_activo VARCHAR(100)  NULL,
    CONSTRAINT pk_pienso PRIMARY KEY (id_pienso)
)
    ENGINE = InnoDB;

-- 6. LOTE_PIENSO

CREATE TABLE LOTE_PIENSO
(
    id_lote_pienso      INT AUTO_INCREMENT,
    codigo_lote_fabrica VARCHAR(50)                            NOT NULL,
    id_pienso           INT                                    NOT NULL,
    fecha_fabricacion   DATE                                   NOT NULL,
    fecha_recepcion     DATE                                   NOT NULL,
    fecha_caducidad     DATE                                   NOT NULL,
    sacos_recibidos     DECIMAL(7, 2)                          NOT NULL,
    sacos_disponibles   DECIMAL(7, 2)                          NOT NULL,
    estado_lote         ENUM ('ACTIVO', 'AGOTADO', 'RETIRADO') NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT pk_lote_pienso PRIMARY KEY (id_lote_pienso),
    CONSTRAINT uq_lote_pienso_codigo UNIQUE (codigo_lote_fabrica),
    CONSTRAINT ck_sacos_disponibles CHECK (sacos_disponibles >= 0.0),
    CONSTRAINT fk_lote_pienso_pienso FOREIGN KEY (id_pienso)
        REFERENCES PIENSO (id_pienso)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_lote_pienso_fefo ON LOTE_PIENSO (estado_lote, fecha_caducidad);

-- 7. LOTE_PECES
CREATE TABLE LOTE_PECES
(
    id_lote_peces         INT AUTO_INCREMENT,
    codigo_lote           VARCHAR(50)                                                 NOT NULL,
    id_especie            INT                                                         NOT NULL,
    id_jaula_actual       INT                                                         NOT NULL,
    fecha_siembra         DATE                                                        NOT NULL,
    cantidad_inicial      INT                                                         NOT NULL,
    cantidad_actual       INT                                                         NOT NULL,
    peso_medio_inicial_g  DECIMAL(6, 2)                                               NOT NULL,
    peso_medio_actual_g   DECIMAL(6, 2)                                               NOT NULL,
    fecha_ultimo_muestreo DATE                                                        NULL,
    biomasa_actual_kg     DECIMAL(9, 2)                                               NOT NULL,
    estado_lote           ENUM ('EN_ENGORDE', 'COSECHADO', 'FUSIONADO', 'TRASLADADO') NOT NULL DEFAULT 'EN_ENGORDE',
    CONSTRAINT pk_lote_peces PRIMARY KEY (id_lote_peces),
    CONSTRAINT uq_lote_peces_codigo UNIQUE (codigo_lote),
    CONSTRAINT ck_cantidad_actual CHECK (cantidad_actual >= 0),
    CONSTRAINT fk_lote_peces_especie FOREIGN KEY (id_especie)
        REFERENCES ESPECIE (id_especie) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_lote_peces_jaula FOREIGN KEY (id_jaula_actual)
        REFERENCES JAULA (id_jaula) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_lote_peces_jaula_estado ON LOTE_PECES (id_jaula_actual, estado_lote);

-- 8. HISTORICO_TRASLADO

CREATE TABLE HISTORICO_TRASLADO
(
    id_traslado                INT AUTO_INCREMENT,
    id_lote_peces              INT          NOT NULL,
    id_jaula_origen            INT          NOT NULL,
    id_jaula_destino           INT          NOT NULL,
    cantidad_peces_trasladados INT          NOT NULL,
    fecha_hora_traslado        DATETIME     NOT NULL,
    motivo                     VARCHAR(150) NOT NULL,
    id_usuario                 INT          NOT NULL,
    CONSTRAINT pk_historico_traslado PRIMARY KEY (id_traslado),
    CONSTRAINT ck_peces_trasladados CHECK (cantidad_peces_trasladados >= 0),
    CONSTRAINT fk_traslado_lote FOREIGN KEY (id_lote_peces)
        REFERENCES LOTE_PECES (id_lote_peces) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_traslado_origen FOREIGN KEY (id_jaula_origen)
        REFERENCES JAULA (id_jaula) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_traslado_destino FOREIGN KEY (id_jaula_destino)
        REFERENCES JAULA (id_jaula) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_traslado_usuario FOREIGN KEY (id_usuario)
        REFERENCES USUARIO (id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_traslado_lote ON HISTORICO_TRASLADO (id_lote_peces);

-- 9. PROGRAMACION_TOMA
CREATE TABLE PROGRAMACION_TOMA
(
    id_programacion       INT AUTO_INCREMENT,
    id_lote_peces         INT                                           NOT NULL,
    fecha_programada      DATE                                          NOT NULL,
    numero_toma           TINYINT                                       NOT NULL DEFAULT 1,
    total_tomas_dia       TINYINT                                       NOT NULL DEFAULT 1,
    biomasa_calculo_kg    DECIMAL(9, 2)                                 NOT NULL,
    peso_medio_calculo_g  DECIMAL(6, 2)                                 NOT NULL,
    temperatura_calculo_c DECIMAL(4, 2)                                 NOT NULL,
    tasa_sfr_porc         DECIMAL(4, 2)                                 NOT NULL,
    racion_teorica_kg     DECIMAL(7, 2)                                 NOT NULL,
    sacos_programados     DECIMAL(5, 2)                                 NOT NULL,
    estado_orden          ENUM ('PENDIENTE', 'REALIZADA', 'SUSPENDIDA') NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT pk_programacion_toma PRIMARY KEY (id_programacion),
    CONSTRAINT uq_programacion_toma UNIQUE (id_lote_peces, fecha_programada, numero_toma),
    CONSTRAINT fk_programacion_lote_peces FOREIGN KEY (id_lote_peces)
        REFERENCES LOTE_PECES (id_lote_peces) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_prog_fecha_estado ON PROGRAMACION_TOMA (fecha_programada, estado_orden);

-- 10. REGISTRO_CONSUMO

CREATE TABLE REGISTRO_CONSUMO
(
    id_registro_consumo       INT AUTO_INCREMENT,
    id_programacion           INT      NOT NULL,
    hora_inicio               DATETIME NOT NULL,
    hora_fin                  DATETIME NOT NULL,
    respuesta_alimentaria     TINYINT  NOT NULL,
    id_usuario                INT      NOT NULL,
    fecha_hora_sincronizacion DATETIME NULL,
    CONSTRAINT pk_registro_consumo PRIMARY KEY (id_registro_consumo),
    CONSTRAINT uq_consumo_programacion UNIQUE (id_programacion),
    CONSTRAINT ck_respuesta_alimentaria CHECK (respuesta_alimentaria BETWEEN 1 AND 4),
    CONSTRAINT fk_consumo_programacion FOREIGN KEY (id_programacion)
        REFERENCES PROGRAMACION_TOMA (id_programacion) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_consumo_usuario FOREIGN KEY (id_usuario)
        REFERENCES USUARIO (id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
)
    ENGINE = InnoDB;

-- 11. LINEA_CONSUMO_PIENSO

CREATE TABLE LINEA_CONSUMO_PIENSO
(
    id_linea_consumo    INT AUTO_INCREMENT,
    id_registro_consumo INT           NOT NULL,
    id_lote_pienso      INT           NOT NULL,
    sacos_gastados      DECIMAL(5, 2) NOT NULL,
    CONSTRAINT pk_linea_consumo PRIMARY KEY (id_linea_consumo),
    CONSTRAINT ck_sacos_gastados CHECK (sacos_gastados >= 0.0),
    CONSTRAINT fk_linea_consumo FOREIGN KEY (id_registro_consumo)
        REFERENCES REGISTRO_CONSUMO (id_registro_consumo) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_linea_lote_pienso FOREIGN KEY (id_lote_pienso)
        REFERENCES LOTE_PIENSO (id_lote_pienso) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_linea_consumo_lote ON LINEA_CONSUMO_PIENSO (id_lote_pienso);

-- 12. REGISTRO_MORTALIDAD

CREATE TABLE REGISTRO_MORTALIDAD
(
    id_mortalidad       INT AUTO_INCREMENT,
    id_lote_peces       INT                                                             NOT NULL,
    fecha               DATE                                                            NOT NULL,
    peces_muertos       INT                                                             NOT NULL,
    causa_probable      ENUM ('NATURAL_DESCARTE', 'ESTRES', 'PATOLOGIA', 'DESCONOCIDA') NOT NULL DEFAULT 'NATURAL_DESCARTE',
    id_usuario          INT                                                             NOT NULL,
    fecha_hora_registro DATETIME                                                        NOT NULL,
    CONSTRAINT pk_registro_mortalidad PRIMARY KEY (id_mortalidad),
    CONSTRAINT ck_peces_muertos CHECK (peces_muertos > 0),
    CONSTRAINT fk_mortalidad_lote_peces FOREIGN KEY (id_lote_peces)
        REFERENCES LOTE_PECES (id_lote_peces) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_mortalidad_usuario FOREIGN KEY (id_usuario)
        REFERENCES USUARIO (id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_mortalidad_lote_fecha ON REGISTRO_MORTALIDAD (id_lote_peces, fecha);

-- 13. REGISTRO_PESCA
CREATE TABLE REGISTRO_PESCA
(
    id_pesca            INT AUTO_INCREMENT,
    id_lote_peces       INT           NOT NULL,
    fecha               DATE          NOT NULL,
    peces_capturados    INT           NOT NULL,
    biomasa_extraida_kg DECIMAL(9, 2) NOT NULL,
    peso_medio_pesca_g  DECIMAL(6, 2) NOT NULL,
    destino_comercial   VARCHAR(100)  NOT NULL,
    albaran_pesca       VARCHAR(50)   NULL,
    id_usuario          INT           NOT NULL,
    fecha_hora_registro DATETIME      NOT NULL,
    CONSTRAINT pk_registro_pesca PRIMARY KEY (id_pesca),
    CONSTRAINT ck_peces_capturados CHECK (peces_capturados >= 0),
    CONSTRAINT ck_biomasa_extraida CHECK (biomasa_extraida_kg >= 0.0),
    CONSTRAINT fk_pesca_lote_peces FOREIGN KEY (id_lote_peces)
        REFERENCES LOTE_PECES (id_lote_peces) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_pesca_usuario FOREIGN KEY (id_usuario)
        REFERENCES USUARIO (id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_pesca_lote_fecha ON REGISTRO_PESCA (id_lote_peces, fecha);

-- 14. INCIDENCIA_MANTENIMIENTO

CREATE TABLE INCIDENCIA_MANTENIMIENTO
(
    id_incidencia   INT AUTO_INCREMENT,
    id_jaula        INT                                                                                          NOT NULL,
    fecha_hora      DATETIME                                                                                     NOT NULL,
    tipo_incidencia ENUM ('ROTURA_RED', 'DESPERFECTO_FLOTADOR', 'CABO_FONDEO', 'DEPREDADOR', 'TEMPORAL', 'OTRO') NOT NULL,
    gravedad        ENUM ('BAJA', 'MEDIA', 'URGENTE')                                                            NOT NULL,
    descripcion     TEXT                                                                                         NOT NULL,
    foto_url        VARCHAR(255)                                                                                 NULL,
    estado          ENUM ('PENDIENTE', 'EN_REPARACION', 'RESUELTA')                                              NOT NULL DEFAULT 'PENDIENTE',
    id_usuario      INT                                                                                          NOT NULL,
    CONSTRAINT pk_incidencia PRIMARY KEY (id_incidencia),
    CONSTRAINT fk_incidencia_jaula FOREIGN KEY (id_jaula)
        REFERENCES JAULA (id_jaula) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_incidencia_usuario FOREIGN KEY (id_usuario)
        REFERENCES USUARIO (id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;
CREATE INDEX idx_incidencia_jaula_estado ON INCIDENCIA_MANTENIMIENTO (id_jaula, estado);

-- TRIGGERS DE SEGURIDAD

DELIMITER $$

CREATE TRIGGER tg_check_jaula_ocupada_insert
    BEFORE INSERT
    ON LOTE_PECES
    FOR EACH ROW
BEGIN
    DECLARE v_ocupada INT DEFAULT 0;
    IF NEW.estado_lote = 'EN_ENGORDE' THEN
        SELECT COUNT(*)
        INTO v_ocupada
        FROM LOTE_PECES
        WHERE id_jaula_actual = NEW.id_jaula_actual
          AND estado_lote = 'EN_ENGORDE';
        IF v_ocupada > 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Error de Integridad: La jaula seleccionada ya alberga un lote activo en engorde.';
        END IF;
    END IF;
END $$

CREATE TRIGGER tg_check_jaula_ocupada_update
    BEFORE UPDATE
    ON LOTE_PECES
    FOR EACH ROW
BEGIN
    DECLARE v_ocupada INT DEFAULT 0;
    IF NEW.estado_lote = 'EN_ENGORDE' THEN
        SELECT COUNT(*)
        INTO v_ocupada
        FROM LOTE_PECES
        WHERE id_jaula_actual = NEW.id_jaula_actual
          AND estado_lote = 'EN_ENGORDE'
          AND id_lote_peces <> NEW.id_lote_peces;
        IF v_ocupada > 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Error de Integridad: La jaula de destino ya alberga un lote activo en engorde.';
        END IF;
    END IF;
END $$

DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;