SELECT inst.descripcion as descripcion,inst.id as codigo
FROM instituciones inst,proyectos pr,proyectos_asignado_usuario pruser
WHERE  inst.id=pr.instituciones_id AND pr.id=pruser.proyectos_id AND pruser.usuarios_id=