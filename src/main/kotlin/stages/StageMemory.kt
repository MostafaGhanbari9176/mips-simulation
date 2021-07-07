package stages

import model.PCSource
import javax.inject.Inject

class StageMemory @Inject constructor() {

    fun pcSource():PCSource{
        return PCSource.NextPC
    }

}