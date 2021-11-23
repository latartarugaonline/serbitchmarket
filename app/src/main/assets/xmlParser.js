function parseXml(attr, value, innerText) {

	switch (attr.toLowerCase()) {

		case "ngv":
		    if(value == 1)
                printFromJsApi.printer_setNegativeMode(true);
            else
                 printFromJsApi.printer_setNegativeMode(false);
			break;

		case "flp":
		    if(value == 1)
		        printFromJsApi.printer_setFlip(true);
		    else
		        printFromJsApi.printer_setFlip(false);
			break;

		case "rtt":
		    if(value == 1)
                 printFromJsApi.printer_setRotateEnabled(true);
            else
                printFromJsApi.printer_setRotateEnabled(false);
			break;

		case "line":
		    printFromJsApi.printer_setNewLines(value);
			break;

		case "spc":
		    printFromJsApi.printer_setCharSpacing(value);
			break;

		case "alg":
		    printFromJsApi.printer_setAlign(value.toLowerCase());
			break;

		case "fnt":
            printFromJsApi.printer_setFont(value);
			break;

		case "bld":
		    if(value == 1)
                printFromJsApi.printer_setBold(true);
            else
                printFromJsApi.printer_setBold(false);
			break;

		case "udl":
             if(value == 1)
                printFromJsApi.printer_setUnderline(true);
            else
                printFromJsApi.printer_setUnderline(false);
			break;

		case "size":
            printFromJsApi.printer_setTextSize(value);
			break;

		case "frm":
		    if(value == "txt")
                printFromJsApi.printer_setText(innerText);
            if(value == "bc")
                printFromJsApi.printer_setText(innerText);
			break;

		case "bc":
		    printFromJsApi.printer_setText(innerText);
			break;

		case "txt":
		    printFromJsApi.printer_setText(value);
			break;

		case "grp":
            printFromJsApi.printer_setText(innerText);
			break;

		default:
			break;


	}


}