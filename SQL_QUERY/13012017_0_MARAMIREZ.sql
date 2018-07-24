ALTER TABLE proyectos ADD COLUMN proyecto_tipo integer DEFAULT 0
UPDATE proyectos SET proyecto_tipo=1 WHERE id=8;