drop database if exists DBlaboratorio;
create database DBlaboratorio;
use DBlaboratorio;


create table Categorias (
    IdCategoria int auto_increment,
    Nombre varchar(50) not null,
    Descripcion text,
    Tipo enum('Dispositivo', 'Componente', 'Accesorio', 'Periférico'),
    constraint pk_Categorias primary key (IdCategoria)
);

create table Proveedores (
    IdProveedor int auto_increment,
    Nombre varchar(100) not null,
    Telefono varchar(20),
    Email varchar(100),
    Direccion text,
    Especialidad varchar(100),
    constraint pk_Proveedores primary key(IdProveedor)
);

create table Productos (
    IdProducto int auto_increment,
    Nombre varchar(100) not null,
    Descripcion text,
    Marca varchar(50) not null,
    Modelo varchar(50) not null,
    PrecioVenta decimal(10, 2) not null,
    StockMinimo int default 3,
    CategoriaId int,
    GarantiaMeses int,
    Color varchar(30),
    PesoKg decimal(5, 2),
    Dimensiones varchar(30),
    urlImagen varchar (255),
    FechaCreacion datetime default current_timestamp,
    constraint pk_Productos primary key (IdProducto),
    foreign key (CategoriaId) references Categorias(IdCategoria)
);

create table EspecificacionesTecnicas (
    IdEspecificacion int auto_increment,
    ProductoId int not null,
    Caracteristica varchar(100) not null,
    Valor varchar(100) not null,
    Unidad varchar(20),
    constraint pk_EspecificacionesTecnicas primary key (IdEspecificacion),
    foreign key (ProductoId) references Productos(IdProducto) on delete cascade
);

create table ProductoProveedor (
    IdRelacion int auto_increment,
    ProductoId int not null,
    ProveedorId int not null,
    PrecioCompra decimal(10, 2),
    TiempoEntregaDias int,
    constraint pk_ProductoProveedor primary key(IdRelacion),
    foreign key (ProductoId) references Productos(IdProducto),
    foreign key (ProveedorId) references Proveedores(IdProveedor)
);

create table Clientes (
    IdCliente int auto_increment,
    Nombre varchar(100) not null,
    Apellidos varchar(100),
    Telefono varchar(20),
    Email varchar(100),
    Direccion text,
    FechaRegistro datetime default current_timestamp,
    Activo boolean default true,
    constraint pk_Clientes primary key(IdCliente)
);

create table usuarios(
	idUsuario int auto_increment,
	nombre varchar(100) not null,
	contrasena varchar (100) not null,
	constraint pk_usuarios primary key (idUsuario)
);

create table Ventas (
    IdVenta int auto_increment,
    ClienteId int,
    FechaVenta datetime default current_timestamp,
    Subtotal decimal(10, 2) not null,
    Iva decimal(10, 2) not null,
    Total decimal(10, 2) not null,
    MetodoPago enum('Efectivo', 'Tarjeta', 'Transferencia', 'Meses sin intereses'),
    Facturada enum ("Facturada","No Facturada"),
    FolioFactura varchar(50),
    constraint pk_Ventas primary key (IdVenta),
    foreign key (ClienteId) references Clientes(IdCliente)
);

create table DetalleVenta (
    IdDetalle int auto_increment,
    VentaId int not null,
    ProductoId int not null,
    Cantidad int not null,
    PrecioUnitario decimal(10, 2) not null,
    Importe decimal(10, 2) not null,
    constraint pk_DetalleVenta primary key (IdDetalle),
    foreign key (VentaId) references Ventas(IdVenta) on delete cascade,
    foreign key (ProductoId) references Productos(IdProducto)
);

create table MovimientosInventario (
    IdMovimiento int auto_increment,
    ProductoId int not null,
    TipoMovimiento enum('Entrada', 'Salida', 'Ajuste', 'Devolución'),
    Cantidad int not null,
    FechaMovimiento varchar (100) not null,
    VentaId int ,
    ProveedorId int,
    Comentario text,
    constraint pk_MovimientosInventario primary key (IdMovimiento),
    foreign key (ProductoId) references Productos(IdProducto),
    foreign key (VentaId) references Ventas(IdVenta),
    foreign key (ProveedorId) references Proveedores(IdProveedor)
);

delimiter $$
create procedure sp_AgregarCategoria(
    in p_Nombre varchar(50),
    in p_Descripcion text,
    in p_Tipo enum('Dispositivo', 'Componente', 'Accesorio', 'Periférico')
)
begin
    insert into Categorias(Nombre, Descripcion, Tipo)
    values(p_Nombre, p_Descripcion, p_Tipo);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarCategorias()
begin
    select * from Categorias;
end $$
delimiter ;


delimiter $$
create procedure sp_ActualizarCategoria(
    in p_IdCategoria int,
    in p_Nombre varchar(50),
    in p_Descripcion text,
    in p_Tipo enum('Dispositivo', 'Componente', 'Accesorio', 'Periférico')

)
begin
    update Categorias 
    set Nombre = p_Nombre, 
        Descripcion = p_Descripcion, 
        Tipo = p_Tipo
    where IdCategoria = p_IdCategoria;
end $$
delimiter ;

delimiter $$
create procedure sp_EliminarCategoria(in p_IdCategoria int)
begin
    delete from Categorias where IdCategoria = p_IdCategoria;
end $$
delimiter ;


delimiter $$
create procedure sp_AgregarProveedor(
    in p_Nombre varchar(100),
    in p_Telefono varchar(20),
    in p_Email varchar(100),
    in p_Direccion text,
    in p_Especialidad varchar(100)
)
begin
    insert into Proveedores(Nombre, Telefono, Email, Direccion, Especialidad)
    values(p_Nombre, p_Telefono, p_Email, p_Direccion, p_Especialidad);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarProveedores()
begin
    select * from Proveedores;
end $$
delimiter ;

delimiter $$
create procedure sp_ActualizarProveedor(
    in p_IdProveedor int,
    in p_Nombre varchar(100),
    in p_Telefono varchar(20),
    in p_Email varchar(100),
    in p_Direccion text,
    in p_Especialidad varchar(100)

)
begin
    update proveedores 
    set nombre = p_Nombre, 
        telefono = p_Telefono, 
        email = p_Email, 
        direccion = p_Direccion, 
        especialidad = p_Especialidad

    where idproveedor = p_IdProveedor;
end $$
delimiter ;



delimiter $$
create procedure sp_EliminarProveedor(in p_IdProveedor int)
begin
    delete from Proveedores where IdProveedor = p_IdProveedor;
end $$
delimiter ;


delimiter $$
create procedure sp_AgregarProducto(
    in p_Nombre varchar(100),
    in p_Descripcion text,
    in p_Marca varchar(50),
    in p_Modelo varchar(50),
    in p_PrecioVenta decimal(10,2),
    in p_StockMinimo int,
    in p_CategoriaId int,
    in p_GarantiaMeses int,
    in p_Color varchar(30),
    in p_PesoKg decimal(5,2),
    in p_Dimensiones varchar(30),
    in p_urlImagen varchar (255)
)
begin
    insert into Productos(Nombre, Descripcion, Marca, Modelo, PrecioVenta, StockMinimo, CategoriaId, GarantiaMeses, Color, PesoKg, Dimensiones,urlImagen)
    values( p_Nombre, p_Descripcion, p_Marca, p_Modelo, p_PrecioVenta,p_StockMinimo, p_CategoriaId, p_GarantiaMeses, p_Color, p_PesoKg, p_Dimensiones, p_urlImagen);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarProductos()
begin
    select * from Productos;
end $$
delimiter ;


delimiter $$
create procedure sp_ActualizarProducto(
    in p_IdProducto int,
    in p_Nombre varchar(100),
    in p_Descripcion text,
    in p_Marca varchar(50),
    in p_Modelo varchar(50),
    in p_PrecioVenta decimal(10,2),
    in p_StockMinimo int,
    in p_CategoriaId int,
    in p_GarantiaMeses int,
    in p_Color varchar(30),
    in p_PesoKg decimal(5,2),
    in p_Dimensiones varchar(30),
    in p_urlImagen varchar (255)
)
begin
    update productos 
    set 
        nombre = p_Nombre, 
        descripcion = p_Descripcion, 
        marca = p_Marca, 
        modelo = p_Modelo, 
        precioventa = p_PrecioVenta,  
        stockminimo = p_StockMinimo, 
        categoriaid = p_CategoriaId, 
        garantiameses = p_GarantiaMeses, 
        color = p_Color, 
        pesokg = p_PesoKg, 
        dimensiones = p_Dimensiones,
        urlImagen = p_urlImagen
    where idproducto = p_IdProducto;
end $$
delimiter ;

delimiter $$
create procedure sp_EliminarProducto(in p_IdProducto int)
begin
    delete from Productos where IdProducto = p_IdProducto;
end $$
delimiter ;


delimiter $$
create procedure sp_AgregarEspecificacion(
    in p_ProductoId int,
    in p_Caracteristica varchar(100),
    in p_Valor varchar(100),
    in p_Unidad varchar(20)
)
begin
    insert into EspecificacionesTecnicas(ProductoId, Caracteristica, Valor, Unidad)
    values(p_ProductoId, p_Caracteristica, p_Valor, p_Unidad);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarEspecificacionesProducto()
begin
    select * from EspecificacionesTecnicas;
end $$
delimiter ;

delimiter $$
create procedure sp_ActualizarEspecificacion(
    in p_IdEspecificacion int,
    in p_ProductoId int,
    in p_Caracteristica varchar(100),
    in p_Valor varchar(100),
    in p_Unidad varchar(20)
)
begin
    update especificacionestecnicas 
    set productoid = p_ProductoId, 
        caracteristica = p_Caracteristica, 
        valor = p_Valor, 
        unidad = p_Unidad
    where idespecificacion = p_IdEspecificacion;
end $$
delimiter ;


delimiter $$
create procedure sp_EliminarEspecificacion(in p_IdEspecificacion int)
begin
    delete from EspecificacionesTecnicas where IdEspecificacion = p_IdEspecificacion;
end $$
delimiter ;


delimiter $$
create procedure sp_AgregarCliente(
    in p_Nombre varchar(100),
    in p_Apellidos varchar(100),
    in p_Telefono varchar(20),
    in p_Email varchar(100),
    in p_Direccion text,
	in p_FechaRegistro datetime
)
begin
    insert into Clientes(Nombre, Apellidos, Telefono, Email, Direccion, FechaRegistro)
    values(p_Nombre, p_Apellidos, p_Telefono, p_Email, p_Direccion, p_FechaRegistro);
end $$
delimiter ;


-- Modificar el procedimiento de listado para mostrar solo activos
DELIMITER $$
CREATE PROCEDURE sp_ListarClientes()
BEGIN
    SELECT * FROM Clientes WHERE Activo = TRUE;
END$$
DELIMITER ;


delimiter $$
create procedure sp_ActualizarCliente(
    in p_IdCliente int,
    in p_Nombre varchar(100),
    in p_Apellidos varchar(100),
    in p_Telefono varchar(20),
    in p_Email varchar(100),
    in p_Direccion text,
    in p_FechaRegistro datetime

)
begin
    update clientes 
    set nombre = p_Nombre, 
        apellidos = p_Apellidos, 
        telefono = p_Telefono, 
        email = p_Email, 
        direccion = p_Direccion,
        fechaRegistro = p_FechaRegistro 
    where idcliente = p_IdCliente;
end $$
delimiter ;

-- Modificar el procedimiento de eliminación
DELIMITER $$
CREATE PROCEDURE sp_EliminarCliente(IN p_IdCliente INT)
BEGIN
    UPDATE Clientes SET Activo = FALSE WHERE IdCliente = p_IdCliente;
END$$
DELIMITER ;


delimiter $$
create procedure sp_AgregarVenta(
    in p_ClienteId int,
    in p_Subtotal decimal(10,2),
    in p_Iva decimal(10,2),
    in p_Total decimal(10,2),
    in p_MetodoPago enum('Efectivo', 'Tarjeta', 'Transferencia', 'Meses sin intereses'),
    in p_Facturada enum ("Facturada","No Facturada"),
    in p_FolioFactura varchar(50),
    out p_VentaId int
)
begin
    insert into Ventas(ClienteId, Subtotal, Iva, Total, MetodoPago,  Facturada, FolioFactura)
    values(p_ClienteId, p_Subtotal, p_Iva, p_Total, p_MetodoPago,  p_Facturada, p_FolioFactura);
    
end $$
delimiter ;

delimiter $$
create procedure sp_ListarVentas()
begin
    select * from Ventas;
end $$
delimiter ;

delimiter $$
create procedure sp_ActualizarVenta(
    in p_IdVenta int,
    in p_ClienteId int,
    in p_Subtotal decimal(10,2),
    in p_Iva decimal(10,2),
    in p_Total decimal(10,2),
    in p_MetodoPago enum('Efectivo', 'Tarjeta', 'Transferencia', 'Meses sin intereses'),
    in p_Facturada enum ("Facturada","No Facturada"), 
    in p_FolioFactura varchar(50)
)
begin
    update ventas 
    set clienteid = p_ClienteId, 
        subtotal = p_Subtotal, 
        iva = p_Iva, 
        total = p_Total, 
        metodopago = p_MetodoPago, 
        facturada = p_Facturada, 
        foliofactura = p_FolioFactura
    where idventa = p_IdVenta;
end $$
delimiter ;

delimiter $$
create procedure sp_EliminarVenta(in p_IdVenta int)
begin
    delete from Ventas where IdVenta = p_IdVenta;
end $$
delimiter ;


delimiter $$
create procedure sp_AgregarDetalleVenta(
    in p_VentaId int,
    in p_ProductoId int,
    in p_Cantidad int,
    in p_PrecioUnitario decimal(10,2),
    in p_Importe decimal(10,2)
)
begin
    insert into DetalleVenta(VentaId, ProductoId, Cantidad, PrecioUnitario, Importe)
    values(p_VentaId, p_ProductoId, p_Cantidad, p_PrecioUnitario, p_Importe);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarDetalleVenta()
begin
    select * from DetalleVenta;
end $$
delimiter ;

delimiter $$
create procedure sp_ActualizarDetalleVenta(
    in p_IdDetalle int,
    in p_VentaId int,
    in p_ProductoId int,
    in p_Cantidad int,
    in p_PrecioUnitario decimal(10,2),
    in p_Importe decimal(10,2)
)
begin
    update detalleventa 
    set ventaid = p_VentaId, 
        productoid = p_ProductoId, 
        cantidad = p_Cantidad, 
        preciounitario = p_PrecioUnitario, 
        importe = p_Importe
    where iddetalle = p_IdDetalle;
end $$
delimiter ;


delimiter $$
create procedure sp_EliminarDetalleVenta(in p_IdDetalle int)
begin
    delete from DetalleVenta where IdDetalle = p_IdDetalle;
end $$
delimiter ;

delimiter $$
create procedure sp_ListarMovimientosInventario()
begin
    select * from MovimientosInventario;

end $$
delimiter ;

delimiter $$
create procedure sp_EliminarMovimientosInventario(in p_IdMovimiento int)
begin
    delete from MovimientosInventario where IdMovimiento = p_IdMovimiento;
end $$
delimiter ;

delimiter $$
create procedure sp_agregarmovimientoinventario(
    in p_productoid int,
    in p_tipomovimiento enum('entrada', 'salida', 'ajuste', 'devolución'),
    in p_cantidad int,
    in p_fechamovimiento varchar (100),
    in p_ventaid int,
    in p_proveedorid int,
    in p_comentario text,
    out p_idmovimiento int
)
begin
    insert into movimientosinventario(productoid, tipomovimiento, cantidad, fechamovimiento, ventaid, proveedorid, comentario)
    values(p_productoid, p_tipomovimiento, p_cantidad, p_fechamovimiento, p_ventaid, p_proveedorid, p_comentario);
    
end $$
delimiter ;


delimiter $$
create procedure sp_ActualizarMovimientoInventario(
    in p_IdMovimiento int,
    in p_ProductoId int,
    in p_TipoMovimiento enum('Entrada', 'Salida', 'Ajuste', 'Devolución'),
    in p_Cantidad int,
    in p_FechaMovimiento varchar(100),
    in p_VentaId int,
    in p_ProveedorId int,
    in p_Comentario text
)
begin
    update MovimientosInventario 
    set 
        ProductoId = p_ProductoId, 
        TipoMovimiento = p_TipoMovimiento, 
        Cantidad = p_Cantidad,
        FechaMovimiento = p_FechaMovimiento,
        VentaId = p_VentaId, 
        ProveedorId = p_ProveedorId, 
        Comentario = p_Comentario
    where IdMovimiento = p_IdMovimiento;
end $$
delimiter ;

delimiter $$
create procedure sp_AgregarProductoProveedor(
    in p_ProductoId int,
    in p_ProveedorId int,
    in p_PrecioCompra decimal(10,2),
    in p_TiempoEntregaDias int
)
begin
    insert into ProductoProveedor(ProductoId, ProveedorId,PrecioCompra, TiempoEntregaDias)
    values(p_ProductoId, p_ProveedorId, p_PrecioCompra, p_TiempoEntregaDias);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarProductoProveedor()
begin
    select * from ProductoProveedor;

end $$
delimiter ;

delimiter $$
create procedure sp_ActualizarProductoProveedor(
    in p_IdRelacion int,
    in p_ProductoId int,
    in p_ProveedorId int,
    in p_PrecioCompra decimal(10,2),
    in p_TiempoEntregaDias int
)
begin
    update productoproveedor 
    set productoid = p_ProductoId, 
        proveedorid = p_ProveedorId, 
        preciocompra = p_PrecioCompra, 
        tiempoentregadias = p_TiempoEntregaDias
    where idrelacion = p_IdRelacion;
end $$
delimiter ;

delimiter $$
create procedure sp_EliminarProductoProveedor(in p_IdRelacion int)
begin
    delete from ProductoProveedor where IdRelacion = p_IdRelacion;
end $$
delimiter ;


delimiter //
	create procedure sp_AgregarUsuarios(
		in p_nombre varchar(100),
		in p_contrasena varchar(16)
		)
		begin
			insert into Usuarios(nombre, contrasena)
			values(p_nombre, p_contrasena);
		end//
delimiter ;

delimiter //
	create procedure sp_ListarUsuarios()
	begin
		select
			U.nombre,
            U.contrasena
		from Usuarios U;
	end//
 
delimiter ;

call sp_AgregarUsuarios("Messi","goat");
call sp_AgregarUsuarios("hola","a");



call sp_AgregarCategoria('Laptops', 'Computadoras portátiles de diferentes marcas y especificaciones', 'Dispositivo');
call sp_AgregarCategoria('Procesadores', 'Unidades centrales de procesamiento para computadoras', 'Componente');
call sp_AgregarCategoria('Teclados', 'Dispositivos de entrada para computadoras', 'Periférico');
call sp_AgregarCategoria('Mouse', 'Dispositivos apuntadores para computadoras', 'Periférico');
call sp_AgregarCategoria('Monitores', 'Pantallas para visualización de contenido', 'Dispositivo');
call sp_AgregarCategoria('Memorias RAM', 'Memoria de acceso aleatorio para computadoras', 'Componente');
call sp_AgregarCategoria('Discos Duros', 'Dispositivos de almacenamiento de datos', 'Componente');
call sp_AgregarCategoria('Tarjetas Gráficas', 'Procesadores dedicados para gráficos', 'Componente');
call sp_AgregarCategoria('Impresoras', 'Dispositivos para imprimir documentos', 'Dispositivo');
call sp_AgregarCategoria('Audífonos', 'Dispositivos de audio personal', 'Accesorio');

call sp_AgregarProveedor('TecnoSuministros', '5551234567', 'contacto@tecnosuministros.com', 'Av. Tecnología 123, CDMX', 'Componentes electrónicos');
call sp_AgregarProveedor('CompuParts', '5557654321', 'ventas@compuparts.mx', 'Calle Circuito 45, Guadalajara', 'Partes de computadora');
call sp_AgregarProveedor('DigitalWare', '5559876543', 'info@digitalware.com.mx', 'Blvd. Digital 678, Monterrey', 'Equipos completos');
call sp_AgregarProveedor('ElectroMundo', '5552345678', 'contacto@electromundo.mx', 'Av. Innovación 910, Puebla', 'Electrónicos en general');
call sp_AgregarProveedor('TechImport', '5558765432', 'importaciones@techimport.com', 'Calle Globalización 111, Tijuana', 'Importación de tecnología');
call sp_AgregarProveedor('PCExpress', '5553456789', 'servicio@expres.com.mx', 'Av. Velocidad 222, Querétaro', 'Venta rápida de componentes');
call sp_AgregarProveedor('GadgetPro', '5556543210', 'proveeduria@gadgetpro.mx', 'Conexión 333, León', 'Dispositivos innovadores');
call sp_AgregarProveedor('InnovaTech', '5554321098', 'soporte@innova.tech', 'Av. Progreso 444, Mérida', 'Tecnología de vanguardia');
call sp_AgregarProveedor('CompuGlobal', '5553210987', 'compras@compuglobal.net', 'Calle Internacional 555, Cancún', 'Distribuidor mundial');
call sp_AgregarProveedor('HardwareSolutions', '5557890123', 'soluciones@hardwaresol.com', 'Av. Componentes 666, Toluca', 'Soluciones en hardware');

call sp_AgregarProducto('Laptop EliteBook', 'Laptop empresarial de alto rendimiento', 'HP', 'EliteBook 840 G7', 25000.00, 5, 1, 24, 'Plata', 1.45, '32.4 x 21.8 x 1.79 cm', '001');
call sp_AgregarProducto('Procesador Core i7', 'Procesador de 10ma generación', 'Intel', 'i7-10700K', 6500.00, 10, 2, 36, 'verde', 0.05, '37.5 x 37.5 mm', '002');
call sp_AgregarProducto('Teclado mecánico', 'Teclado mecánico para gaming', 'Logitech', 'G Pro X', 2200.00, 8, 3, 12, 'Negro', 0.98, '44 x 15 x 3.3 cm', '003');
call sp_AgregarProducto('Mouse inalámbrico', 'Mouse ergonómico inalámbrico', 'Microsoft', 'Sculpt Ergonomic', 1200.00, 15, 4, 12, 'Negro', 0.15, '12 x 7 x 4 cm', '004');
call sp_AgregarProducto('Monitor 27" 4K', 'Monitor UHD con HDR', 'Dell', 'UltraSharp U2720Q', 12000.00, 6, 5, 24, 'rojo', 5.8, '61.1 x 36.4 x 21.6 cm', '005');
call sp_AgregarProducto('Memoria DDR4 16GB', 'Memoria RAM de alta velocidad', 'Corsair', 'Vengeance LPX', 1800.00, 20, 6, 24, 'celeste', 0.03, '13.3 x 3.4 x 0.5 cm', '006');
call sp_AgregarProducto('SSD 1TB NVMe', 'Disco de estado sólido ultrarrápido', 'Samsung', '970 EVO Plus', 3500.00, 12, 7, 60, 'marron', 0.01, '8 x 2.2 x 0.2 cm', '007');
call sp_AgregarProducto('Tarjeta Gráfica RTX 3080', 'Tarjeta gráfica para gaming', 'NVIDIA', 'RTX 3080 Founders', 25000.00, 4, 8, 36, 'Negro/Plateado', 1.4, '28.5 x 11.2 x 5.1 cm', '008');
call sp_AgregarProducto('Impresora multifuncional', 'Impresora láser a color', 'Brother', 'MFC-L3750CDW', 8500.00, 7, 9, 12, 'Blanco', 28.5, '42 x 39.8 x 42.6 cm', '009');
call sp_AgregarProducto('Audífonos inalámbricos', 'Audífonos con cancelación de ruido', 'Sony', 'WH-1000XM4', 7000.00, 9, 10, 12, 'aqua', 0.25, '18.5 x 16.8 x 6.9 cm', '010');

call sp_AgregarEspecificacion(1, 'Procesador', 'Intel Core i7-1165G7','DDR5' );
call sp_AgregarEspecificacion(1, 'RAM', '16GB', 'DDR4');
call sp_AgregarEspecificacion(2, 'Núcleos', '8', 'DDR8');
call sp_AgregarEspecificacion(2, 'Frecuencia base', '3.8', 'GHz');
call sp_AgregarEspecificacion(3, 'Tipo de switches', 'GX Blue Clicky', 'DDR3');
call sp_AgregarEspecificacion(3, 'Retroiluminación', 'RGB', 'DDR6');
call sp_AgregarEspecificacion(4, 'DPI máximo', '4000', 'DDR7');
call sp_AgregarEspecificacion(4, 'Conectividad', 'Bluetooth + USB', 'DDR3');
call sp_AgregarEspecificacion(5, 'Tasa de refresco', '60', 'Hz');
call sp_AgregarEspecificacion(5, 'Tipo de panel', 'IPS', 'DDR7');

call sp_AgregarProductoProveedor(1,1,25.50,3);
call sp_AgregarProductoProveedor(2,3,18.75,5);
call sp_AgregarProductoProveedor(4,2,42.99,2);
call sp_AgregarProductoProveedor(5,5,33.25,7);
call sp_AgregarProductoProveedor(3,4,15.80,4);
call sp_AgregarProductoProveedor(6,6,29.99,1);
call sp_AgregarProductoProveedor(7,8,55.50,6);
call sp_AgregarProductoProveedor(9,7,12.25,3);
call sp_AgregarProductoProveedor(10,9,75.30,5);
call sp_AgregarProductoProveedor(8,10,22.45,2);

CALL sp_AgregarCliente('Juan', 'Pérez García', '5551234567', 'juan.perez@email.com', 'Calle Primavera 123, CDMX', '2023-01-15 10:30:00');
CALL sp_AgregarCliente('María', 'López Martínez', '5559876543', 'maria.lopez@email.com', 'Av. Reforma 456, CDMX', '2023-02-20 14:15:00');
CALL sp_AgregarCliente('Carlos', 'González Fernández', '5552345678', 'carlos.gonzalez@email.com', 'Calle Roble 789, MTY', '2023-03-10 09:45:00');
CALL sp_AgregarCliente('Ana', 'Rodríguez Sánchez', '5558765432', 'ana.rodriguez@email.com', 'Blvd. Díaz Ordaz 321, GDL', '2023-04-05 16:20:00');
CALL sp_AgregarCliente('Pedro', 'Hernández Jiménez', '5553456789', 'pedro.hernandez@email.com', 'Calle Pino 654, PUE', '2023-05-12 11:10:00');
CALL sp_AgregarCliente('Laura', 'Martínez Díaz', '5557654321', 'laura.martinez@email.com', 'Av. Universidad 987, QRO', '2023-06-18 13:25:00');
CALL sp_AgregarCliente('Jorge', 'Díaz Romero', '5554567890', 'jorge.diaz@email.com', 'Calle Cedro 135, TJ', '2023-07-22 15:40:00');
CALL sp_AgregarCliente('Sofía', 'Sánchez Pérez', '5556543210', 'sofia.sanchez@email.com', 'Av. Hidalgo 246, LEÓN', '2023-08-30 12:00:00');
CALL sp_AgregarCliente('Miguel', 'Ramírez Torres', '5555678901', 'miguel.ramirez@email.com', 'Calle Nogal 579, MER', '2023-09-05 17:30:00');
CALL sp_AgregarCliente('Patricia', 'Flores Castro', '5556789012', 'patricia.flores@email.com', 'Av. Colón 864, CUL', '2023-10-10 08:45:00');

call sp_AgregarVenta(1, 25000.00, 4000.00, 29000.00, 'Tarjeta',  'Facturada', 'FAC-00001', @ventaId);
call sp_AgregarVenta(2, 6500.00, 1040.00, 7540.00, 'Transferencia',  'Facturada', 'FAC-00002', @ventaId);
call sp_AgregarVenta(3, 2200.00, 352.00, 2552.00, 'Efectivo',  'No Facturada', 'FAC-00003', @ventaId);
call sp_AgregarVenta(4, 1200.00, 192.00, 1392.00, 'Tarjeta',  'Facturada', 'FAC-00004', @ventaId);
call sp_AgregarVenta(5, 12000.00, 1920.00, 13920.00, 'Meses sin intereses',  'Facturada', 'FAC-00005', @ventaId);
call sp_AgregarVenta(6, 1800.00, 288.00, 2088.00, 'Transferencia',  'No Facturada', 'FAC-00006', @ventaId);
call sp_AgregarVenta(7, 3500.00, 560.00, 4060.00, 'Tarjeta',  'Facturada', 'FAC-00007', @ventaId);
call sp_AgregarVenta(8, 25000.00, 4000.00, 29000.00, 'Meses sin intereses', 'Facturada', 'FAC-00008', @ventaId);
call sp_AgregarVenta(9, 8500.00, 1360.00, 9860.00, 'Transferencia',  'Facturada', 'FAC-00009', @ventaId);
call sp_AgregarVenta(10, 7000.00, 1120.00, 8120.00, 'Efectivo', 'No Facturada', 'FAC-00010', @ventaId);

call sp_AgregarDetalleVenta(1, 1, 1, 25000.00, 25000.00);
call sp_AgregarDetalleVenta(2, 2, 1, 6500.00, 6500.00);
call sp_AgregarDetalleVenta(3, 3, 1, 2200.00, 2200.00);
call sp_AgregarDetalleVenta(4, 4, 1, 1200.00, 1200.00);
call sp_AgregarDetalleVenta(5, 5, 1, 12000.00, 12000.00);
call sp_AgregarDetalleVenta(6, 6, 2, 1800.00, 3600.00);
call sp_AgregarDetalleVenta(7, 7, 1, 3500.00, 3500.00);
call sp_AgregarDetalleVenta(8, 8, 1, 25000.00, 25000.00);
call sp_AgregarDetalleVenta(9, 9, 1, 8500.00, 8500.00);
call sp_AgregarDetalleVenta(10, 10, 1, 7000.00, 7000.00);

call sp_agregarmovimientoinventario(1, 'entrada',  50,  '2023-11-01',  2,  1,      'compra inicial de stock',  @nuevoid);
call sp_agregarmovimientoinventario(3, 'salida',    2,  '2023-11-02',  4,  4,      'venta al cliente 45',      @nuevoid);
call sp_agregarmovimientoinventario(5, 'entrada',  30,  '2023-11-03',  6,  2,      'reposición de inventario', @nuevoid);
call sp_agregarmovimientoinventario(2, 'salida',    1,  '2023-11-04',  8,  8,      'venta mostrador',          @nuevoid);
call sp_agregarmovimientoinventario(4, 'ajuste',    5,  '2023-11-05',  10, 10,     'corrección por conteo físico', @nuevoid);
call sp_agregarmovimientoinventario(7, 'devolución', 1,  '2023-11-06',  5 , 2,    'devolución por garantía', @nuevoid);
call sp_agregarmovimientoinventario(6, 'entrada',  1,  '2023-11-07',     6,   4,   'pedido especial',         @nuevoid);
call sp_agregarmovimientoinventario(8, 'salida',    3,  '2023-11-08',   1,  1,    'venta en línea',        @nuevoid);
call sp_agregarmovimientoinventario(9, 'ajuste',    2,  '2023-11-09',  4,  3,    'pérdida por daño',       @nuevoid);
call sp_agregarmovimientoinventario(10, 'entrada',  5, '2023-01-02',  3,   2,    'última compra del mes',      @nuevoid);

    