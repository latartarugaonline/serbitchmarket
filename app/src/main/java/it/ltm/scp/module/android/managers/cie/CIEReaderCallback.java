package it.ltm.scp.module.android.managers.cie;

import it.ltm.scp.module.android.model.CIE;

public interface CIEReaderCallback
{
    void onSuccess(CIE iCie);
    void onFailure(int iReason, String iReasonString);
}
