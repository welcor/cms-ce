if ( !lpt )
{
    var lpt = {};
}

lpt.WorkerUtility = {
    isWorkerSupported:function ()
    {
        return !(Worker == undefined);
    }
};

lpt.ArrayUtility = {
    shiftAndAdd:function ( array, value )
    {
        for ( var i = 1; i < array.length; i++ )
        {
            array[ i - 1 ] = array[ i ];
        }

        array[ array.length - 1 ] = value;
    },
    initialize:function ( array, value )
    {
        for ( var i = 0; i < array.length; i++ )
        {
            if ( array[i] == undefined )
            {
                array[i] = value;
            }
        }
    },
    isInitialized:function ( array )
    {
        if ( array.length == 0 )
        {
            return false;
        }

        return array[ 0 ] != undefined;
    }
};





