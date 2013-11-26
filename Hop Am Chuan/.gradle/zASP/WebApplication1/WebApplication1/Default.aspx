<%@ Page Language="vb" AutoEventWireup="false" CodeBehind="Default.aspx.vb" Inherits="WebApplication1._Default" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title>Hello!</title>
</head>
<body>
    <h1>Đây là chương trình ASP của tôi!</h1>
    <form id="form1" runat="server">
    <div>
        <asp:CheckBox ID="CheckBox1" runat="server" />
        <asp:Button ID="Button1" runat="server" Text="Button" />
        <asp:LinkButton ID="LinkButton1"
            runat="server">LinkButton</asp:LinkButton>
    
    </div>
    </form>
</body>
</html>
