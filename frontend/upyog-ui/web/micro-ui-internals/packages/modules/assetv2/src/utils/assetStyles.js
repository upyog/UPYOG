

export const assetStyles = {
    toolTip:{
        width: "12px", 
        height: "5px", 
        marginLeft: "10px", 
        display: "inline-flex", 
        alignItems: "center"
    },

    toolTipText:{
        whiteSpace: "pre-wrap", 
        fontSize: "small", 
        wordWrap: "break-word", 
        width: "300px", 
        marginLeft: "15px", 
        marginBottom: "-10px"
    },

    formGridStyles:{
        display: "grid", 
        gridTemplateColumns: "1fr 1fr", 
        gap: "22px", 
        marginBottom: "30px", 
        border: "1px solid rgb\(101 43 43\)", 
        borderRadius: "10px", 
        padding: "16px"
    },

    rupeeIcon:{
        position: 'absolute',
        left: '10px',
        top: '50%',
        transform: 'translateY(-50%)',
        pointerEvents: 'none',
        zIndex: 1,
        color: '#505A5F',
        fontSize: '16px'
    },

    rupeeIconPostions:{
        position: 'relative', 
        width: '100%'
    }
}