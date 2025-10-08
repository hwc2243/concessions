function confirmDelete(id){
    var name = $(id).data('name');
    $('#deleteItem').text(name);
    var href = $(id).data('href');
    $('#deleteButton').attr("href", href);
    $('#deleteModal').modal('show');
}