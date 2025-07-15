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
    foreign key (ClienteId) references Clientes(IdCliente),
    foreign key (UsuarioId) references Usuarios(IdUsuario)  
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
    UsuarioId int not null,
    Comentario text,
    constraint pk_MovimientosInventario primary key (IdMovimiento),
    foreign key (ProductoId) references Productos(IdProducto),
    foreign key (UsuarioId) references Usuarios(IdUsuario),
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
    in p_PrecioUnitario decimal(10,2)
)
begin
    insert into DetalleVenta(VentaId, ProductoId, Cantidad, PrecioUnitario, Importe)
    values(p_VentaId, p_ProductoId, p_Cantidad, p_PrecioUnitario, v_Importe);
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
    in p_UsuarioId int,
    in p_Comentario text
)
begin
    insert into MovimientosInventario(ProductoId, TipoMovimiento, Cantidad, VentaId, ProveedorId, UsuarioId, Comentario)
    values(p_ProductoId, p_TipoMovimiento, p_Cantidad, p_VentaId, p_ProveedorId, p_UsuarioId, p_Comentario);
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