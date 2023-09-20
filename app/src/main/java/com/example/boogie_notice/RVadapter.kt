package com.example.boogie_notice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVadapter(val List: MutableList<contentsModel>):RecyclerView.Adapter<RVadapter.ViewHolder> (){


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){


        fun BindItems(item: contentsModel, position :Int){

            val rv_text=itemView.findViewById<TextView>(R.id.rvTextArea)

            rv_text.text="${position+1} . "+item.titleText

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.rvitem,parent,false)
        val ViewHolder=ViewHolder(view)


        return ViewHolder
    }




    interface ItemClick{

        fun OnClick(view:View,position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view:View,position: Int)
    }
    var itemClick: ItemClick?=null
    var itemLongClick: OnItemLongClickListener?=null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(itemClick!=null){
            holder.itemView.setOnClickListener{
                view->itemClick!!.OnClick(view,position)

            }

        }
        if(itemLongClick!=null){

            holder.itemView.setOnLongClickListener{
                    view->itemLongClick!!.onItemLongClick(view,position)
                false
            }

        }
        holder.BindItems(List[position],position)
    }

    override fun getItemCount(): Int {
        return List.size
    }


}