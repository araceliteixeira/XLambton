package com.aa.xlambton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.aa.xlambton.Model.AgentMissionDAO;
import com.aa.xlambton.Model.Mission;
import com.aa.xlambton.Model.MissionDAO;

import java.util.List;

public class MissionHistoricActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_historic);

        Long agentId = getIntent().getLongExtra("agentId",0);
        if (agentId != null && agentId > 0 ){
            AgentMissionDAO dao = new AgentMissionDAO(this);
            List<Mission> missions = dao.dbSearchMissionsFromAgentById(this,agentId);
            dao.close();
            if (missions.isEmpty() || missions == null) {
                Toast.makeText(MissionHistoricActivity.this,"There are no missions related to this agent.", Toast.LENGTH_LONG).show();
            } else {
                HistoricListAdapter adapter = new HistoricListAdapter(this, R.layout.activity_mission_historic, missions);
                ListView missionList = (ListView) findViewById(R.id.historic_mission_list);
                missionList.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_back:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
