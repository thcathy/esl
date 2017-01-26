package com.esl;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.faces.config.PhaseBinding;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;

@RewriteConfiguration
public class RewriteConfigurationProvider extends HttpConfigurationProvider
{

    @Override
    public int priority()
    {
        return 10;
    }

    @Override
    public Configuration getConfiguration(final ServletContext context)
    {
        return ConfigurationBuilder.begin()
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/startivp")))
                .perform(Forward.to("/practice/irregularverb/start.jsf"))

                .addRule()
                .when(Direction.isInbound().and(Path.matches("/practice/phoneticsymbolpractice/input")))
                .perform(Forward.to("/practice/phoneticsymbolpractice/input.jsf"))

                .addRule(
                        Join.path("/manage/vocabimage/{fromid}").to("/manage/vocabimage.jsf")
                ).perform(PhaseOperation.enqueue(
                        Invoke.binding(El.retrievalMethod("vocabImageController.listImage"))
                ).after(PhaseId.RESTORE_VIEW))
                .where("fromid").bindsTo(PhaseBinding.to(El.property("vocabImageController.fromId")).after(PhaseId.RESTORE_VIEW))

                ;
    }
}