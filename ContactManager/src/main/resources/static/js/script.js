
/** const toggleSidebar =()=>{
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("diaplay","none");
		$(".sidebar").css("width","0%");
		$(".sidebar a").css("width","0%");
		$(".content").css("margin-left","0%");
	}else{
		$(".sidebar").css("diaplay","block");
		$(".content").css("margin-left","15%");
	}
};*/
/* Set the width of the sidebar to 250px and the left margin of the page content to 250px */
function openNav() {
  document.getElementById("mySidebar").style.width = "250px";
  document.getElementById("main").style.marginLeft = "250px";
}

/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
function closeNav() {
  document.getElementById("mySidebar").style.width = "0";
  document.getElementById("main").style.marginLeft = "0";
}