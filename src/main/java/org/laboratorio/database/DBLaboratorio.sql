 -- drop database if exists DBlaboratorio;
create database DBlaboratorio;
use DBlaboratorio;


create table Categorias (
    IdCategoria int auto_increment,
    Nombre varchar(50) not null,
    Descripcion text,
    Tipo enum('Dispositivo', 'Componente', 'Accesorio', 'Periférico'),
    SeccionId int,
    constraint pk_Categorias primary key (IdCategoria)
);

create table Proveedores (
    IdProveedor int auto_increment,
    Nombre varchar(100) not null,
    ContactoPrincipal varchar(100),
    Telefono varchar(20),
    Email varchar(100),
    Direccion text,
    Especialidad varchar(100),
    PaisOrigen varchar(50),
    Rfc varchar(20),
    Activo boolean default true,
    constraint pk_Proveedores primary key(IdProveedor)
);

create table Productos (
    IdProducto int auto_increment,
    CodigoBarras varchar(50) unique,
    Nombre varchar(100) not null,
    Descripcion text,
    Marca varchar(50) not null,
    Modelo varchar(50) not null,
    PrecioVenta decimal(10, 2) not null,
    PrecioCompra decimal(10, 2) not null,
    Existencia int not null default 0,
    StockMinimo int default 3,
    CategoriaId int,
    GarantiaMeses int,
    Color varchar(30),
    PesoKg decimal(5, 2),
    Dimensiones varchar(30),
    Activo boolean default true,
    FechaCreacion datetime default current_timestamp,
    FechaActualizacion datetime on update current_timestamp,
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
    CodigoProveedor varchar(50),
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
    Rfc varchar(20),
    FechaRegistro datetime default current_timestamp,
    Activo boolean default true,
    constraint pk_Clientes primary key(IdCliente)
);

create table usuarios(
    IdUsuario int auto_increment, 
    nombre varchar(100) not null,
    contrasena varchar (100) not null,
    constraint pk_usuarios primary key (IdUsuario)  
);

create table Ventas (
    IdVenta int auto_increment,
    ClienteId int,
    FechaVenta datetime default current_timestamp,
    Subtotal decimal(10, 2) not null,
    Iva decimal(10, 2) not null,
    Total decimal(10, 2) not null,
    MetodoPago enum('Efectivo', 'Tarjeta', 'Transferencia', 'Meses sin intereses'),
    UsuarioId int,
    Facturada boolean default false,
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
    FechaMovimiento datetime default current_timestamp,
    VentaId int null,
    ProveedorId int null,
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
    in p_Tipo enum('Dispositivo', 'Componente', 'Accesorio', 'Periférico'),
    in p_SeccionId int
)
begin
    insert into Categorias(Nombre, Descripcion, Tipo, SeccionId)
    values(p_Nombre, p_Descripcion, p_Tipo, p_SeccionId);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarCategorias()
begin
    select * from Categorias;
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
    in p_ContactoPrincipal varchar(100),
    in p_Telefono varchar(20),
    in p_Email varchar(100),
    in p_Direccion text,
    in p_Especialidad varchar(100),
    in p_PaisOrigen varchar(50),
    in p_Rfc varchar(20)
)
begin
    insert into Proveedores(Nombre, ContactoPrincipal, Telefono, Email, Direccion, Especialidad, PaisOrigen, Rfc)
    values(p_Nombre, p_ContactoPrincipal, p_Telefono, p_Email, p_Direccion, p_Especialidad, p_PaisOrigen, p_Rfc);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarProveedores()
begin
    select * from Proveedores;
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
    in p_CodigoBarras varchar(50),
    in p_Nombre varchar(100),
    in p_Descripcion text,
    in p_Marca varchar(50),
    in p_Modelo varchar(50),
    in p_PrecioVenta decimal(10,2),
    in p_PrecioCompra decimal(10,2),
    in p_StockMinimo int,
    in p_CategoriaId int,
    in p_GarantiaMeses int,
    in p_Color varchar(30),
    in p_PesoKg decimal(5,2),
    in p_Dimensiones varchar(30)
)
begin
    insert into Productos(CodigoBarras, Nombre, Descripcion, Marca, Modelo, PrecioVenta, PrecioCompra, StockMinimo, CategoriaId, GarantiaMeses, Color, PesoKg, Dimensiones)
    values(p_CodigoBarras, p_Nombre, p_Descripcion, p_Marca, p_Modelo, p_PrecioVenta, p_PrecioCompra, p_StockMinimo, p_CategoriaId, p_GarantiaMeses, p_Color, p_PesoKg, p_Dimensiones);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarProductos()
begin
    select * from Productos;
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
    in p_Rfc varchar(20)
)
begin
    insert into Clientes(Nombre, Apellidos, Telefono, Email, Direccion, Rfc)
    values(p_Nombre, p_Apellidos, p_Telefono, p_Email, p_Direccion, p_Rfc);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarClientes()
begin
    select * from Clientes;
end $$
delimiter ;


delimiter $$
create procedure sp_EliminarCliente(in p_IdCliente int)
begin
    delete from Clientes where IdCliente = p_IdCliente;
end $$
delimiter ;


delimiter $$
create procedure sp_AgregarVenta(
    in p_ClienteId int,
    in p_Subtotal decimal(10,2),
    in p_Iva decimal(10,2),
    in p_Total decimal(10,2),
    in p_MetodoPago enum('Efectivo', 'Tarjeta', 'Transferencia', 'Meses sin intereses'),
    in p_UsuarioId int,
    in p_Facturada boolean,
    in p_FolioFactura varchar(50),
    out p_VentaId int
)
begin
    insert into Ventas(ClienteId, Subtotal, Iva, Total, MetodoPago, UsuarioId, Facturada, FolioFactura)
    values(p_ClienteId, p_Subtotal, p_Iva, p_Total, p_MetodoPago, p_UsuarioId, p_Facturada, p_FolioFactura);
    
end $$
delimiter ;

delimiter $$
create procedure sp_ListarVentas()
begin
    select * from Ventas;
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
create procedure sp_EliminarDetalleVenta(in p_IdDetalle int)
begin
    delete from DetalleVenta where IdDetalle = p_IdDetalle;
end $$
delimiter ;


delimiter $$
create procedure sp_AgregarMovimientoInventario(
    in p_ProductoId int,
    in p_TipoMovimiento enum('Entrada', 'Salida', 'Ajuste', 'Devolución'),
    in p_Cantidad int,
    in p_VentaId int,
    in p_ProveedorId int,
    in p_Comentario text
)
begin
    insert into MovimientosInventario(ProductoId, TipoMovimiento, Cantidad, VentaId, ProveedorId,Comentario)
    values(p_ProductoId, p_TipoMovimiento, p_Cantidad, p_VentaId, p_ProveedorId, p_Comentario);
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
create procedure sp_AgregarProductoProveedor(
    in p_ProductoId int,
    in p_ProveedorId int,
    in p_CodigoProveedor varchar(50),
    in p_PrecioCompra decimal(10,2),
    in p_TiempoEntregaDias int
)
begin
    insert into ProductoProveedor(ProductoId, ProveedorId, CodigoProveedor, PrecioCompra, TiempoEntregaDias)
    values(p_ProductoId, p_ProveedorId, p_CodigoProveedor, p_PrecioCompra, p_TiempoEntregaDias);
end $$
delimiter ;


delimiter $$
create procedure sp_ListarProductoProveedor()
begin
    select * from ProductoProveedor;

end $$
delimiter ;


delimiter $$
create procedure sp_EliminarProductoProveedor(in p_IdRelacion int)
begin
    delete from ProductoProveedor where IdRelacion = p_IdRelacion;
end $$
delimiter ;



call sp_AgregarCategoria('Smartphones', 'Teléfonos inteligentes', 'Dispositivo', 1);
call sp_AgregarCategoria('Laptops', 'Computadoras portátiles', 'Dispositivo', 1);
call sp_AgregarCategoria('Procesadores', 'Unidades centrales de procesamiento', 'Componente', 2);
call sp_AgregarCategoria('Tarjetas Gráficas', 'GPU para renderizado', 'Componente', 2);
call sp_AgregarCategoria('Memorias RAM', 'Memoria de acceso aleatorio', 'Componente', 2);
call sp_AgregarCategoria('Audífonos', 'Dispositivos de audio personal', 'Accesorio', 3);
call sp_AgregarCategoria('Teclados', 'Dispositivos de entrada', 'Periférico', 3);
call sp_AgregarCategoria('Mouse', 'Dispositivos señaladores', 'Periférico', 3);
call sp_AgregarCategoria('Monitores', 'Pantallas de visualización', 'Dispositivo', 1);
call sp_AgregarCategoria('Monitores', 'Pantallas de visualización', 'Dispositivo', 1);

call sp_ListarCategorias();

call sp_AgregarProveedor('TecnoSuministros', 'Juan Pérez', '5551234567', 'contacto@tecnosuministros.com', 'Av. Tecnología 123, CDMX', 'Componentes PC', 'México', 'TSUM800101ABC');
call sp_AgregarProveedor('ElectroParts', 'María García', '5557654321', 'ventas@electroparts.com', 'Calle Circuito 456, GDL', 'Electrónica en general', 'México', 'ELPA800202DEF');
call sp_AgregarProveedor('DistriTec', 'Carlos López', '5559876543', 'info@distritec.com', 'Blvd. Innovación 789, MTY', 'Dispositivos móviles', 'México', 'DITC800303GHI');
call sp_AgregarProveedor('TechGlobal', 'Ana Martínez', '5552345678', 'contact@techglobal.com', '123 Tech Ave, California', 'Componentes de alta gama', 'EE.UU.', 'TGLB800404JKL');
call sp_AgregarProveedor('AsiaElectronics', 'Li Wei', '5558765432', 'sales@asiaelectronics.com', '456 Innovation Rd, Shenzhen', 'Fabricante directo', 'China', 'ASEL800505MNO');
call sp_AgregarProveedor('CompuAbastece', 'Roberto Sánchez', '5553456789', 'ventas@compuabastece.com', 'Av. Digital 321, PUE', 'Suministros de cómputo', 'México', 'COAB800606PQR');
call sp_AgregarProveedor('AudioPro', 'Laura Fernández', '5556543210', 'contacto@audiopro.com', 'Calle Sonido 654, QRO', 'Equipo de audio', 'México', 'AUPR800707STU');
call sp_AgregarProveedor('GadgetMasters', 'David Ramírez', '5554567890', 'info@gadgetmasters.com', 'Paseo Tecnológico 987, TIJ', 'Dispositivos innovadores', 'México', 'GAMA800808VWX');
call sp_AgregarProveedor('PCComponentes', 'Sofía Castro', '5555678901', 'ventas@pccomponentes.com', 'Av. Hardware 135, MÉR', 'Componentes para PC', 'México', 'PCCO800909YZA');
call sp_AgregarProveedor('MobileWorld', 'José González', '5556789012', 'atencion@mobileworld.com', 'Blvd. Smartphones 246, CUL', 'Dispositivos móviles', 'México', 'MOWO801010BCD');

call sp_ListarProveedores();

call sp_AgregarProducto('7501001234567', 'iPhone 13', 'Smartphone Apple con chip A15', 'Apple', 'iPhone 13', 19999.00, 15000.00, 10, 1, 12, 'Azul', 0.17, '14.67x7.15x0.76 cm');
call sp_AgregarProducto('7501002345678', 'Galaxy S22', 'Smartphone Samsung con Android', 'Samsung', 'Galaxy S22', 17999.00, 14000.00, 8, 1, 12, 'Negro', 0.17, '14.6x7.0x0.76 cm');
call sp_AgregarProducto('7501003456789', 'MacBook Air M1', 'Laptop ultradelgada de Apple', 'Apple', 'MacBook Air M1', 23999.00, 19000.00, 5, 2, 12, 'Plata', 1.29, '30.41x21.24x1.61 cm');
call sp_AgregarProducto('7501004567890', 'ThinkPad X1 Carbon', 'Laptop empresarial ultraligera', 'Lenovo', 'ThinkPad X1 Carbon', 28999.00, 23000.00, 6, 2, 24, 'Negro', 1.13, '32.3x21.7x1.49 cm');
call sp_AgregarProducto('7501005678901', 'Ryzen 9 5900X', 'Procesador AMD 12 núcleos', 'AMD', 'Ryzen 9 5900X', 8999.00, 7000.00, 15, 4, 36, NULL, 0.03, '4.0x4.0x0.5 cm');
call sp_AgregarProducto('7501006789012', 'RTX 3080', 'Tarjeta gráfica NVIDIA', 'NVIDIA', 'RTX 3080', 15999.00, 13000.00, 4, 5, 36, NULL, 1.50, '28.5x11.2x5.2 cm');
call sp_AgregarProducto('7501007890123', 'AirPods Pro', 'Audífonos inalámbricos con ANC', 'Apple', 'AirPods Pro', 5999.00, 4500.00, 12, 7, 12, 'Blanco', 0.05, '5.4x2.1x2.1 cm');
call sp_AgregarProducto('7501008901234', 'MX Master 3', 'Mouse ergonómico avanzado', 'Logitech', 'MX Master 3', 1999.00, 1500.00, 20, 9, 24, 'Gris', 0.14, '12.4x8.5x4.7 cm');
call sp_AgregarProducto('7501009012345', 'SSD 1TB 980 Pro', 'Disco sólido de alta velocidad', 'Samsung', '980 Pro 1TB', 2999.00, 2200.00, 18, 6, 60, NULL, 0.01, '8.0x2.2x0.2 cm');
call sp_AgregarProducto('7501010123456', 'Monitor 27" 4K', 'Monitor UHD con HDR', 'LG', '27UL850-W', 8999.00, 7000.00, 7, 10, 24, 'Negro', 5.80, '61.3x36.8x21.9 cm');

call sp_ListarProductos();

call sp_AgregarEspecificacion(1, 'Pantalla', '6.1', 'pulgadas');
call sp_AgregarEspecificacion(1, 'RAM', '4', 'GB');
call sp_AgregarEspecificacion(1, 'Almacenamiento', '128', 'GB');
call sp_AgregarEspecificacion(2, 'Pantalla', '6.1', 'pulgadas');
call sp_AgregarEspecificacion(2, 'RAM', '8', 'GB');
call sp_AgregarEspecificacion(2, 'Almacenamiento', '128', 'GB');
call sp_AgregarEspecificacion(3, 'Procesador', 'Apple M1', "sin especificar");
call sp_AgregarEspecificacion(3, 'RAM', '8', 'GB');
call sp_AgregarEspecificacion(3, 'Almacenamiento', '256', "sin especificar");
call sp_AgregarEspecificacion(4, 'Procesador', 'Intel i7-1165G7', "sin especificar");
call sp_AgregarEspecificacion(4, 'RAM', '16', "sin especificar");
call sp_AgregarEspecificacion(4, 'Almacenamiento', '512', 'GB');
call sp_AgregarEspecificacion(5, 'Núcleos', '12', "GB");
call sp_AgregarEspecificacion(5, 'Hilos', '24', "GB");
call sp_AgregarEspecificacion(5, 'Frecuencia', '3.7', 'GHz');
call sp_AgregarEspecificacion(6, 'VRAM', '10', 'GB');
call sp_AgregarEspecificacion(6, 'Tipo', 'GDDR6X', "sin especificar");
call sp_AgregarEspecificacion(6, 'Ancho de banda', '760', 'GB/s');
call sp_AgregarEspecificacion(7, 'Cancelación de ruido', 'Sí', "sin especificar");
call sp_AgregarEspecificacion(7, 'Batería', '4.5', 'horas');
call sp_AgregarEspecificacion(7, 'Resistencia al agua', 'IPX4', "sin especificar");
call sp_AgregarEspecificacion(8, 'DPI', '4000', "sin especificar");
call sp_AgregarEspecificacion(8, 'Conectividad', 'Bluetooth/Unifying', "sin especificar");
call sp_AgregarEspecificacion(8, 'Batería', '70', 'días');
call sp_AgregarEspecificacion(9, 'Velocidad lectura', '7000', 'MB/s');
call sp_AgregarEspecificacion(9, 'Velocidad escritura', '5000', 'MB/s');
call sp_AgregarEspecificacion(9, 'Tipo', 'NVMe PCIe 4.0', "sin especificar");
call sp_AgregarEspecificacion(10, 'Resolución', '3840x2160', 'píxeles');
call sp_AgregarEspecificacion(10, 'Tipo panel', 'IPS', "sin especificar");
call sp_AgregarEspecificacion(10, 'Tasa de refresco', '60', 'Hz');

call sp_ListarEspecificacionesProducto();

call sp_AgregarCliente('Juan', 'Pérez López', '5551112233', 'juan.perez@email.com', 'Calle Primavera 123, CDMX', 'PEPJ800101ABC');
call sp_AgregarCliente('María', 'García Martínez', '5552223344', 'maria.garcia@email.com', 'Av. Reforma 456, GDL', 'GAMM800202DEF');
call sp_AgregarCliente('Carlos', 'Rodríguez Sánchez', '5553334455', 'carlos.rodriguez@email.com', 'Blvd. Universidad 789, MTY', 'ROSC800303GHI');
call sp_AgregarCliente('Ana', 'Hernández González', '5554445566', 'ana.hernandez@email.com', 'Calle Roble 321, PUE', 'HEGA800404JKL');
call sp_AgregarCliente('Luis', 'Martínez Díaz', '5555556677', 'luis.martinez@email.com', 'Av. Central 654, QRO', 'MADL800505MNO');
call sp_AgregarCliente('Laura', 'Gómez Ramírez', '5556667788', 'laura.gomez@email.com', 'Paseo de la Paz 987, TIJ', 'GORL800606PQR');
call sp_AgregarCliente('Pedro', 'Sánchez Castro', '5557778899', 'pedro.sanchez@email.com', 'Calle Luna 135, MÉR', 'SACP800707STU');
call sp_AgregarCliente('Sofía', 'Fernández Ruiz', '5558889900', 'sofia.fernandez@email.com', 'Av. Sol 246, CUL', 'FERS800808VWX');
call sp_AgregarCliente('Jorge', 'Díaz Mendoza', '5559990011', 'jorge.diaz@email.com', 'Blvd. Libertad 357, SLRC', 'DIMJ800909YZA');
call sp_AgregarCliente('Patricia', 'Vázquez Ortega', '5550001122', 'patricia.vazquez@email.com', 'Calle Estrella 468, HGO', 'VAOP801010BCD');

call sp_ListarClientes();

call sp_AgregarVenta(1, 18000.00, 2880.00, 20880.00, 'Tarjeta', 2, true, 'FAC-00001', @ventaId);
call sp_AgregarVenta(3, 24000.00, 3840.00, 27840.00, 'Transferencia', 3, true, 'FAC-00002', @ventaId);
call sp_AgregarVenta(5, 9000.00, 1440.00, 10440.00, 'Efectivo', 4, false, null, @ventaId);
call sp_AgregarVenta(2, 6000.00, 960.00, 6960.00, 'Meses sin intereses', 5, true, 'FAC-00003', @ventaId);
call sp_AgregarVenta(4, 16000.00, 2560.00, 18560.00, 'Tarjeta', 6, true, 'FAC-00004', @ventaId);
call sp_AgregarVenta(7, 2000.00, 320.00, 2320.00, 'Efectivo', 7, false, null, @ventaId);
call sp_AgregarVenta(6, 3000.00, 480.00, 3480.00, 'Transferencia', 8, true, 'FAC-00005', @ventaId);
call sp_AgregarVenta(8, 9000.00, 1440.00, 10440.00, 'Meses sin intereses', 9, true, 'FAC-00006', @ventaId);
call sp_AgregarVenta(10, 18000.00, 2880.00, 20880.00, 'Tarjeta', 10, true, 'FAC-00007', @ventaId);
call sp_AgregarVenta(9, 6000.00, 960.00, 6960.00, 'Efectivo', 2, false, null, @ventaId);

call sp_ListarVentas();

call sp_AgregarDetalleVenta(1, 1, 1, 19999.00, 12.30);
call sp_AgregarDetalleVenta(2, 3, 1, 23999.00, 10.20);
call sp_AgregarDetalleVenta(3, 5, 1, 8999.00, 5.20);
call sp_AgregarDetalleVenta(4, 7, 1, 5999.00, 6.30);
call sp_AgregarDetalleVenta(5, 6, 1, 15999.00, 4.12);
call sp_AgregarDetalleVenta(6, 8, 1, 1999.00, 13.20);
call sp_AgregarDetalleVenta(7, 9, 1, 2999.00, 16.45);
call sp_AgregarDetalleVenta(8, 10, 1, 8999.00, 11.10);
call sp_AgregarDetalleVenta(9, 2, 1, 17999.00, 78.50);
call sp_AgregarDetalleVenta(10, 4, 1, 5999.00, 17.85);

call sp_ListarDetalleVenta();

call sp_AgregarMovimientoInventario(1, 'Entrada', 10, null, 4, 'Compra inicial');
call sp_AgregarMovimientoInventario(2, 'Entrada', 8, null, 3,  'Compra inicial');
call sp_AgregarMovimientoInventario(3, 'Entrada', 5, null, 4,  'Compra inicial');
call sp_AgregarMovimientoInventario(4, 'Entrada', 6, null, 6,  'Compra inicial');
call sp_AgregarMovimientoInventario(5, 'Entrada', 15, null, 2,  'Compra inicial');
call sp_AgregarMovimientoInventario(6, 'Entrada', 4, null, 2,  'Compra inicial');
call sp_AgregarMovimientoInventario(7, 'Entrada', 12, null, 4,  'Compra inicial');
call sp_AgregarMovimientoInventario(8, 'Entrada', 20, null, 7,  'Compra inicial');
call sp_AgregarMovimientoInventario(9, 'Entrada', 18, null, 3,  'Compra inicial');
call sp_AgregarMovimientoInventario(10, 'Entrada', 7, null, 9,  'Compra inicial');

call sp_ListarMovimientosInventario();